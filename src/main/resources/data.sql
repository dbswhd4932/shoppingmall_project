-- 권한 추가
insert into ROLE (role) values ('USER');

-- 카테고리 추가
insert into CATEGORY (category) values ('의류');
insert into CATEGORY (category) values ('신발');

-- 회원 추가 ( 권한 USER )
insert into MEMBER (login_id, password, name, zipcode, detail_address, email, phone, role_id)
values ('테스트1' , '1234', '홍길동', '123-123','상세주소','test1@aaa.com','010-1234-1234-', 1);

insert into MEMBER (login_id, password, name, zipcode, detail_address, email, phone, role_id)
values ('테스트2' , '1234', '둘리', '456-678','상세주소','test2@aaa.com','010-4321-4321-', 1);

-- 카드 추가
insert into CARD ( card_company, card_expire, card_number, member_id )
values ('국민','23-12','1234-1234-1234-1234',1);

insert into CARD ( card_company, card_expire, card_number, member_id )
values ('우리','23-12','4312-4321-4312-4132',1);

-- 상품 추가
--insert into GOODS (member_id, goods_name, price, description, category_id) values (1,'나이키1' , '10000', '설명1', 1);
--insert into GOODS (member_id, goods_name, price, description, category_id) values (1,'나이키2' , '20000', '설명2', 1);
--insert into GOODS (member_id, goods_name, price, description, category_id) values (1,'나이키3' , '30000', '설명3', 1);
--insert into GOODS (member_id, goods_name, price, description, category_id) values (1,'나이키4' , '40000', '설명4', 1);
--insert into GOODS (member_id, goods_name, price, description, category_id) values (1,'나이키5' , '50000', '설명5', 1);
--insert into GOODS (member_id, goods_name, price, description, category_id) values (2,'아디다스1' , '30000', '설명6', 2);
--insert into GOODS (member_id, goods_name, price, description, category_id) values (2,'아디다스2' , '40000', '설명7', 2);
--insert into GOODS (member_id, goods_name, price, description, category_id) values (2,'아디다스3' , '80000', '설명8', 2);
--insert into GOODS (member_id, goods_name, price, description, category_id) values (2,'아디다스4' , '90000', '설명9', 2);
--insert into GOODS (member_id, goods_name, price, description, category_id) values (2,'아디다스5' , '100000', '설명10', 2);


-- 장바구니 추가
--insert into CART (goods_id, total_amount, total_price, member_id) values (1,1, 10000,1);
--insert into CART (goods_id, total_amount, total_price, member_id) values (2,1, 20000,1);
--insert into CART (goods_id, total_amount, total_price, member_id) values (1,1, 10000,2);
--insert into CART (goods_id, total_amount, total_price, member_id) values (2,1, 20000,2);
