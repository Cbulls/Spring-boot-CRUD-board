package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write") // localhost:8080/board/write
    public String boardWriteForm(){
        return "boardwrite";
    }

    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception{
        System.out.println("제목 : " + board.getTitle());
        System.out.println("내용 : " + board.getContent());

        boardService.write(board, file);

        if(board.getTitle().equals("황희윤")){
            model.addAttribute("message", "황희윤님 반갑습니다!");
        } else{
            model.addAttribute("message", "글 작성이 완료되었습니다!");
        }
        model.addAttribute("searchUrl", "/board/list");
        return "message";
    }

    @GetMapping("/board/list")
    public String boardList(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, String searchKeyword) {

        Page<Board> list = null;
        if(searchKeyword == null){
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = pageable.getPageNumber() + 1; // 0에서 시작해서 1 더해줌
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list); // boardService.boardList에서 받은 list를 "list"의 이름으로 넘기겠다.
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "boardList";
    }

    @GetMapping("/board/view") // 상세페이지  localhost:8080/board/view?id=1
    public String boardView(Model model, Integer id){
        model.addAttribute("board", boardService.boardView(id));
        return "boardview";
    }

    // 글 삭제
    @GetMapping("/board/delete")
    public String boardDelete(Integer id, Model model){
        // localhost:8080/board/delete?id=1 하면 1 페이지가 지워진다.
        boardService.boardDelete(id);
        model.addAttribute("message", "글이 삭제 되었습니다!");
        model.addAttribute("searchUrl", "/board/list");
        return "message"; // 삭제하고서 리스트 페이지로 리다이렉트
    }

    // 글 수정
    @GetMapping("/board/modify/{id}")
    // th:href= VS PathVariable 차이 : 둘 다 뒤에 id를 전달하지만 th:href는 view?id=1 이렇게 전달하고 PathVariable은 view/8 로 깔끔하게 전달
    public String boardModify(@PathVariable("id") Integer id, Model model){
        model.addAttribute("board", boardService.boardView(id));
        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception{
        Board boardTemp = boardService.boardView(id); // 임시로 만드는 보드
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        boardService.write(boardTemp, file);
        model.addAttribute("message", "글이 수정되었습니다");
        model.addAttribute("searchUrl", "/board/list");
        // 원래 JPA에서는 수정할 때 이렇게 덮어씌워버리는 방식을 절대 사용하면 안 됩니다.
        // JPA에는 변경 감지(Dirty Checking)이라는 기능이 있어서 트랙잭션 내에서 DB에서 불러온 엔티티(객체)에 수정이 이뤄질 경우 트랙잭션이 끝날 때 자동으로 DB에 반영되기 때문에 변경 감지 기능을 써서 수정해야합니다!
        // 이 영상에서 변경 감지를 쓰지 않고 덮어씌워버린 이유는 변경 감지가 쉬운 개념이 아니라 이것만 다뤄도 내용이 길어지기 때문에 무작정 따라하기라는 취지에 맞지 않기 때문에 다루지 않았습니다.
        // 하지만 JPA 공부하실 때 꼭 학습해야 될 내용이기 때문에 JPA 변경감지, JPA merge, JPA persist 등 꼭 따로 학습하세요!
        return "message";
    }
}
