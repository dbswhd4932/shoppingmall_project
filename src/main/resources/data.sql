insert into ROLE (role) values ('USER');

insert into MEMBER (login_id, password, name, zipcode, detail_address, email, phone, role_id)
values ('테스트ID' , '1234', '홍길동', '123-123','상세주소','test@aaa.com','010-1234-1234-', 1);

insert into GOODS (name, price, description)values ('테스트상품1' , '10000', '설명테스트1');
insert into GOODS (name, price, description)values ('테스트상품2' , '20000', '설명테스트2');