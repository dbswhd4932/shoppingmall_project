-- 권한 추가
--insert into ROLE (role) values ('USER');

-- 카테고리 추가
insert into CATEGORY (category) values ('의류');
insert into CATEGORY (category) values ('신발');

-- 회원 추가 ( 권한 USER )
insert into MEMBER (detail_Address, email, login_Id, login_Type, name , password, phone, zipcode) values
('서울' , 'test1@naver.com', 'test1' , 'NO_SOCIAL' , '홍길동' , '1234' , '010-1234-1234' , '123-123');

insert into MEMBER (detail_Address, email, login_Id, login_Type, name , password, phone, zipcode) values
('경기' , 'test2@naver.com', 'test2' , 'NO_SOCIAL' , '마이콜' , '1234' , '010-4312-4321' , '321-321');

insert into ROLE (role_type , member_id) values ( 'ROLE_USER' , '1');
insert into ROLE (role_type , member_id) values ( 'ROLE_ADMIN' , '1');
insert into ROLE (role_type , member_id) values ( 'ROLE_ADMIN' , '2');

--insert into MEMBER (login_id, password, name, zipcode, detail_address, email, phone)
--values ('테스트2' , '1234', '둘리', '456-678','상세주소','test2@aaa.com','010-4321-4321');