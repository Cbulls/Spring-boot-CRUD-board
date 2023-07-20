package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService {

    // 글 작성
    @Autowired // 스프링의 DI, 만약이게 없으면 private BoardRepository boardRepository = new BoardRepository .... 이런 식이여야 함
    private BoardRepository boardRepository;
    public void write(Board board, MultipartFile file) throws Exception{
        UUID uuid = UUID.randomUUID();

        String fileName = uuid + "_" + file.getOriginalFilename();
        String filePath = "/Users/heeyun/Documents/" + fileName;

        file.transferTo(new File(filePath));

        board.setFilename(fileName);
        board.setFilepath(filePath);

        boardRepository.save(board);
    }

    // 게시글 리스트 불러오기
    public Page<Board> boardList(Pageable pageable){

        return boardRepository.findAll(pageable);
    }

    // 특정 게시글 불러오기
    public Board boardView(Integer id){
        return boardRepository.findById(id).get(); // 그냥 findById하면 optional 값을 가져오기 때문에 get 메서드를 붙여준다.
    }

    // 게시글 삭제
    // void는 리턴이 없음
    public void boardDelete(Integer id){
        boardRepository.deleteById(id);
    }


    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable){
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }
}
