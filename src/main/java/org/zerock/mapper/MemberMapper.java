package org.zerock.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.zerock.domain.AuthVO;
import org.zerock.domain.MemberVO;

import java.util.List;

@Repository
@Mapper
public interface MemberMapper {
    MemberVO readMember(String userId);

    List<MemberVO> readAllMember();

    int deleteAuthOfSpecificMember(String userId);

    int deleteMember(String userId);

    int insertMember(MemberVO member);

    int insertAuthorityToMember(AuthVO auth);
}
