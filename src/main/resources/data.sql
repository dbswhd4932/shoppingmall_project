-- 권한 추가
insert into ROLE (role) values ('USER');

-- 카테고리 추가
insert into ITEM_CATEGORY (category) values ('의류');
insert into ITEM_CATEGORY (category) values ('신발');

-- 회원 추가 ( 권한 USER )
insert into MEMBER (login_id, password, name, zipcode, detail_address, email, phone, role_id)
values ('테스트ID' , '1234', '홍길동', '123-123','상세주소','test@aaa.com','010-1234-1234-', 1);

-- 상품 추가
insert into GOODS (name, price, description, item_category_id)values ('테스트상품1' , '10000', '설명테스트1', 1);
insert into GOODS (name, price, description, item_category_id)values ('테스트상품2' , '20000', '설명테스트2', 2);