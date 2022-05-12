package org.zerock.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.domain.AuthVO;
import org.zerock.domain.MemberVO;
import org.zerock.mapper.MemberMapper;
import org.zerock.security.CustomUser;
import org.zerock.security.CustomUserDetailService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static final Logger log = LogManager.getLogger();

    private final MemberMapper mapper;

    private final PasswordEncoder encoder;

    @Override
    public List<MemberVO> getUserList() {
        return (mapper.readAllMember());
    }

    @Override
    public boolean createUser(MemberVO member, AuthVO auth) {
        boolean status = false;
        member.setPassword(encoder.encode(member.getPassword()));
        if (mapper.insertMember(member) == 1) {
            if (mapper.insertAuthorityToMember(auth) == 1) {
                status = true;
            }
        }
        return status;
    }

    @Override
    public boolean authorizationToUser(AuthVO auth) {
        return mapper.insertAuthorityToMember(auth) == 1 ? true : false;
    }

    @Override
    public boolean deleteUser(String userId) {
        if (mapper.deleteAuthOfSpecificMember(userId) == 1) {
            if (mapper.deleteMember(userId) == 1)
                return true;
            else {
                log.error("Delete Member Failed");
            }
        } else {
            if (mapper.deleteMember(userId) == 1)
                return true;
            else
                log.error("Delete Authority of Member Failed. Please make sure this account is valid");
        }
        return false;
    }

    @Override
    public boolean adminSingleAuthorityDeleteUser() {
        return false;
    }
}
