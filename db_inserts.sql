--gonna need a trigger to make sure usernames on user and admin are unique
--also, do the trigger for the auction-id and bidsn sequences
INSERT INTO administrator values('admin', 'root', 'administrator', '6810 SENSQ', 'admin@1555.com');

INSERT INTO customer values('user0', 'pwd', 'user0', '6810 SENSQ', 'user0@1555.com');
INSERT INTO customer values('user1', 'pwd', 'user1', '6811 SENSQ', 'user1@1555.com');
INSERT INTO customer values('user2', 'pwd', 'user2', '6812 SENSQ', 'user2@1555.com');
INSERT INTO customer values('user3', 'pwd', 'user3', '6813 SENSQ', 'user3@1555.com');
INSERT INTO customer values('user4', 'pwd', 'user4', '6814 SENSQ', 'user4@1555.com');

--Product
INSERT INTO product 
	values((auction_id_increment.NEXTVAL), 'Database', 'SQL ER-design', 'user0', to_date('04-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 50, 2,  'sold', 'user2', to_date('06-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 53);
INSERT INTO product
	values((auction_id_increment.NEXTVAL),'17 inch monitor', '17 inch monitor', 'user0', to_date('06-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 100, 2, 'sold', 'user4', to_date('08-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 110);
INSERT INTO product
	values((auction_id_increment.NEXTVAL),'DELL INSPIRON 1100', 'DELL INSPIRON notebook', 'user0', to_date('07-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 500, 7, 'underauction', NULL, NULL, NULL);
INSERT INTO product
	values((auction_id_increment.NEXTVAL),'Return of the King', 'fantasy', 'user1', to_date('07-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 40, 2, 'sold', 'user2', to_date('09-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 40);
INSERT INTO product 
	values((auction_id_increment.NEXTVAL),'The Sorcerer Stone', 'Harry Porter series', 'user1', to_date('08-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 40, 2, 'sold', 'user3', to_date('10-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 40);
INSERT INTO product 
	values((auction_id_increment.NEXTVAL),'DELL INSPIRON 1100', 'DELL INSPIRON notebook', 'user1', to_date('09-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 200, 1,  'withdrawn', NULL, NULL, NULL);
INSERT INTO product
	values((auction_id_increment.NEXTVAL),'Advanced Database', 'SQL Transaction index', 'user1', to_date('10-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 50, 2, 'underauction', NULL, NULL, 55);

--Bidlog
INSERT INTO bidlog values((bidsn_autoincrement.NEXTVAL), 0, 'user2', to_date('04-dec-07/08:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 50);
INSERT INTO bidlog values((bidsn_autoincrement.NEXTVAL), 0, 'user3', to_date('04-dec-07/09:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 53);
INSERT INTO bidlog values((bidsn_autoincrement.NEXTVAL), 0, 'user2', to_date('05-dec-07/08:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 60);
INSERT INTO bidlog values((bidsn_autoincrement.NEXTVAL), 1, 'user4', to_date('06-dec-07/08:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 100);
INSERT INTO bidlog values((bidsn_autoincrement.NEXTVAL), 1, 'user2', to_date('07-dec-07/08:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 110);
INSERT INTO bidlog values((bidsn_autoincrement.NEXTVAL), 1, 'user4', to_date('07-dec-07/09:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 120);
INSERT INTO bidlog values((bidsn_autoincrement.NEXTVAL), 3, 'user2', to_date('07-dec-07/08:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 40);
INSERT INTO bidlog values((bidsn_autoincrement.NEXTVAL), 4, 'user3', to_date('09-dec-07/08:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 40);
INSERT INTO bidlog values((bidsn_autoincrement.NEXTVAL), 6, 'user2', to_date('07-dec-07/08:00:00am', 'DD-MM-YY/HH:MI:SSAM'), 55);

INSERT INTO catalog (name) 
values('Books'); 
INSERT INTO catalog values('Textbooks', 'Books');
INSERT INTO catalog values('Fiction books', 'Books');
INSERT INTO catalog values('Magazines', 'Books');
INSERT INTO catalog values('Computer Science', 'Textbooks');
INSERT INTO catalog values('Math', 'Textbooks');
INSERT INTO catalog values('Philosophy', 'Textbooks');
INSERT INTO catalog (name)
values('Computer Related');
INSERT INTO catalog values('Desktop PCs', 'Computer Related');
INSERT INTO catalog values('Laptops', 'Computer Related');
INSERT INTO catalog values('Monitors', 'Computer Related');
INSERT INTO catalog values('Computer books', 'Computer Related');

INSERT INTO belongsTo values(0, 'Computer Science');
INSERT INTO belongsTo values(0, 'Computer books');
INSERT INTO belongsTo values(1, 'Monitors');
INSERT INTO belongsTo values(2, 'Laptops');
INSERT INTO belongsTo values(3, 'Fiction books');
INSERT INTO belongsTo values(4, 'Fiction books');
INSERT INTO belongsTo values(5, 'Laptops');
INSERT INTO belongsTo values(6, 'Computer Science');
INSERT INTO belongsTo values(6, 'Computer books');

INSERT INTO ourdate values(to_date('11-dec-07/12:00:00am', 'DD-MM-YY/HH:MI:SSAM'));