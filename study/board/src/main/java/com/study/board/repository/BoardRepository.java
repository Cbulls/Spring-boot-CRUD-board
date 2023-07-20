package com.study.board.repository;

import com.study.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    // JPA repository
    // findBy(컬럼 이름) - 컬럼에서 키워드를 넣어서 찾겠다. ex) 황희윤을 찾고 싶으면 "황희윤" 모두 쳐야 나옴
    // findBy(컬럼 이름) containing - 컬럼에서 키워드가 포함된 것을 찾겠다. ex) 황희윤을 찾고 싶으면 "황"만 쳐도 나옴
    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);
}
