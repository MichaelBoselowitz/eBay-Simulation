---Clear tables

drop table customer cascade constraints;
drop table administrator cascade constraints;
drop table product cascade constraints;
drop table bidlog cascade constraints;
drop table catalog cascade constraints;
drop table belongsto cascade constraints;
drop table ourdate cascade constraints;
drop sequence auction_id_increment;
drop sequence bidsn_autoincrement;

--Create tables

CREATE TABLE customer (
	login		varchar2(10),
	password	varchar2(10),
	name		varchar2(20),
	address		varchar2(30),
	email		varchar2(20),
	Constraint customer_PK primary key (login) deferrable
);

CREATE TABLE administrator (
	login		varchar2(10),
	password	varchar2(10),
	name		varchar2(20),
	address		varchar2(30),
	email		varchar2(20),
	Constraint administrator_PK primary key (login) deferrable
);

CREATE TABLE product (
	auction_id 		int,
	name			varchar2(20),
	description		varchar2(30),
	seller			varchar2(10),
	start_date		date,
	min_price		int,
	number_of_days	int,
	status 			varchar(15)	not null,
	buyer			varchar(10),
	sell_date		date,
	amount			int,
	Constraint product_PK primary key (auction_id) deferrable,
	Constraint product_FK1 foreign key (seller) references customer( login ) initially deferred deferrable,
    Constraint product_FK2 foreign key ( buyer ) references customer( login ) initially deferred deferrable
);

CREATE TABLE bidlog (
	bidsn		int,
	auction_id	int,
	bidder		varchar2(10),
	bid_time	date,
	amount		int,
	Constraint bidlog_PK primary key (bidsn) deferrable,
	Constraint bidlog_FK1 foreign key (auction_id) references product( auction_id ) initially deferred deferrable,
    Constraint bidlog_FK2 foreign key (bidder) references customer( login ) initially deferred deferrable
);

CREATE TABLE catalog (
	name			varchar2(20),
	parent_catalog	varchar2(20),
	Constraint catalog_PK primary key (name) deferrable,
	Constraint catalog_FK foreign key (parent_catalog) references catalog (name) initially deferred deferrable
);

CREATE TABLE belongsTo (
	auction_id		int,
	catalog			varchar2(20),
	Constraint belongsTo_PK primary key (auction_id, catalog),
	Constraint belongsTo_FK foreign key (auction_id) references product(auction_id) initially deferred deferrable,
	Constraint belongsTo_FK2 foreign key (catalog) references catalog (name) initially deferred deferrable
);

CREATE TABLE ourdate (
	c_date		date, --timestamp? or just time?
	Constraint Sys_Table_PK primary key (c_date)
);

--Sequences

CREATE SEQUENCE auction_id_increment
MINVALUE -1
START WITH -1
INCREMENT BY 1;

CREATE SEQUENCE bidsn_autoincrement
MINVALUE -1
START WITH -1
INCREMENT BY 1;

---Procedures

CREATE OR REPLACE PROCEDURE put_product(name in varchar2, description in varchar2, seller in varchar2, min_price in int, number_of_days in int, id out int)
IS
BEGIN
id := auction_id_increment.NEXTVAL;
INSERT INTO Product VALUES(id, name, description, seller, (select c_date from ourdate), min_price, number_of_days, 'underauction', NULL, NULL, NULL);
END;
/

CREATE OR REPLACE PROCEDURE put_BelongsTo(auction_id in int, catalog in varchar2)
IS
t1 int;
t2 int;
BEGIN
SELECT count(*) into t1 FROM Catalog WHERE Parent_Catalog = catalog;
SELECT count(*) into t2 FROM Catalog where name = catalog;
IF t1 = 0 AND t2 != 0
THEN
INSERT INTO Belongsto VALUES(auction_id, catalog);
END IF;
END;
/

---Functions

CREATE OR REPLACE FUNCTION Product_Count (x in int, u in varchar2) RETURN int 
AS
total int;

cursor c1 is
select name from catalog where parent_catalog = u;
t1 int;
BEGIN
select count(*) into t1 from catalog where parent_catalog = u;
IF t1 = 0 THEN
	select count(*) into total
	from belongsTo, product 
	where belongsTo.catalog = u and product.auction_id = belongsTo.auction_id and product.sell_date >= (ADD_MONTHS((SELECT c_date FROM ourdate), (-1)*x));
	return total;
END IF;
total :=0;
for big_category in c1
	loop
		total := total+product_count(x, big_category.name);
	end loop;
	return total;
end;
/

CREATE OR REPLACE FUNCTION Bid_Count (u in varchar2, x in int) RETURN int 
AS
total int;
BEGIN
	SELECT  COUNT(*) into total
	FROM bidlog 
	WHERE bidder = u AND bid_time >= (ADD_MONTHS((SELECT c_date FROM ourdate), (-1)*x));
	return(total);
END;
/

CREATE OR REPLACE FUNCTION Buying_Amount (u in varchar2, x in int) RETURN int 
AS
total int;
BEGIN
	SELECT SUM(amount) into total
	FROM product 
	WHERE buyer = u AND sell_date >= (ADD_MONTHS((SELECT c_date FROM ourdate), (-1)*x)); 
	return(total);
END;
/

---Triggers

CREATE OR REPLACE TRIGGER tri_bidTimeUpdate
AFTER INSERT ON bidlog
FOR EACH ROW
BEGIN
	UPDATE ourdate SET c_date = ((SELECT c_date FROM ourdate)+(5/86400));
END;
/

CREATE OR REPLACE TRIGGER closeAuctions
AFTER UPDATE OF c_date ON ourdate
FOR EACH ROW
BEGIN
UPDATE product SET product.status='closed' 
WHERE (product.start_date+product.number_of_days) <= :new.c_date AND product.status='underauction';
UPDATE product SET sell_date = (product.number_of_days+product.start_date) 
WHERE status='underaction' AND (product.number_of_days+product.start_date) <= :new.c_date;
END;
/

CREATE OR REPLACE TRIGGER tri_updateHighBid
AFTER INSERT ON bidlog
FOR EACH ROW
BEGIN
    UPDATE product SET amount = :new.amount
    WHERE auction_id = :new.auction_id;
END;
/