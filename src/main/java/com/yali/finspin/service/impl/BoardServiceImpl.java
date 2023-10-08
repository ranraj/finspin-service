package com.yali.finspin.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.yali.finspin.domain.Board;
import com.yali.finspin.repository.BoardRepository;
import com.yali.finspin.repository.search.BoardSearchRepository;
import com.yali.finspin.service.BoardService;
import com.yali.finspin.service.TileService;
import com.yali.finspin.service.dto.BoardDTO;
import com.yali.finspin.service.mapper.BoardMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Board}.
 */
@Service
public class BoardServiceImpl implements BoardService {

    private final Logger log = LoggerFactory.getLogger(BoardServiceImpl.class);

    private final BoardRepository boardRepository;

    private final BoardMapper boardMapper;

    private final BoardSearchRepository boardSearchRepository;    
    
    public BoardServiceImpl(BoardRepository boardRepository, BoardMapper boardMapper, BoardSearchRepository boardSearchRepository, TileService tileService) {
        this.boardRepository = boardRepository;
        this.boardMapper = boardMapper;
        this.boardSearchRepository = boardSearchRepository;        
    }

    @Override
    public Mono<BoardDTO> save(BoardDTO boardDTO) {
        log.debug("Request to save Board : {}", boardDTO);
        return boardRepository.save(boardMapper.toEntity(boardDTO)).flatMap(boardSearchRepository::save).map(boardMapper::toDto);
    }

    @Override
    public Mono<BoardDTO> update(BoardDTO boardDTO) {
        log.debug("Request to save Board : {}", boardDTO);
        return boardRepository.save(boardMapper.toEntity(boardDTO)).flatMap(boardSearchRepository::save).map(boardMapper::toDto);
    }

    @Override
    public Mono<BoardDTO> partialUpdate(BoardDTO boardDTO) {
        log.debug("Request to partially update Board : {}", boardDTO);

        return boardRepository
            .findById(boardDTO.getId())
            .map(existingBoard -> {
                boardMapper.partialUpdate(existingBoard, boardDTO);

                return existingBoard;
            })
            .flatMap(boardRepository::save)
            .flatMap(savedBoard -> {
                boardSearchRepository.save(savedBoard);

                return Mono.just(savedBoard);
            })
            .map(boardMapper::toDto);
    }

    @Override
    public Flux<BoardDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Boards");
        Flux<BoardDTO> result = boardRepository.findAllBy(pageable).map(boardMapper::toDto);        
        return result;
    }

    public Mono<Long> countAll() {
        return boardRepository.count();
    }

    public Mono<Long> searchCount() {
        return boardSearchRepository.count();
    }

    @Override
    public Mono<BoardDTO> findOne(String id) {
        log.debug("Request to get Board : {}", id);
        return boardRepository.findById(id).map(boardMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Board : {}", id);
        return boardRepository.deleteById(id).then(boardSearchRepository.deleteById(id));
    }

    @Override
    public Flux<BoardDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Boards for query {}", query);
        return boardSearchRepository.search(query, pageable).map(boardMapper::toDto);
    }
}
