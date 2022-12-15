//package com.project.shop.member.jwt;
//
//import com.project.shop.member.domain.Member;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//@Slf4j
//@Data
//public class PrincipalDetails implements UserDetails {
//
//    private final Member member;
//
//    public PrincipalDetails(Member member) {
//        this.member = member;
//
//        log.info("username = {}" , member.getLoginId());
//        log.info("password = {}" , member.getPassword());
//        log.info("role = {}" , member.getRoles().stream().toList());
//    }
//
//    // 해당 유저의 권한 리턴
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        this.member.getRoles().forEach(role -> {
//            authorities.add(() -> String.valueOf(role));
//        });
//        return authorities;
//    }
//
//    @Override
//    public String getPassword() {
//        return member.getPassword();
//    }
//
//    @Override
//    public String getUsername() {
//        return member.getLoginId();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
