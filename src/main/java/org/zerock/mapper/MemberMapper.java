package org.zerock.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.zerock.domain.MemberVO;

import java.util.List;

@Mapper
@Repository
public interface MemberMapper {
    List<MemberVO> readMemberList();
}