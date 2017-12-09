ALTER TABLE T_PERSON
  ADD CONSTRAINT fk_paid FOREIGN KEY (ADDRESS_ID)
REFERENCES T_ADDRESS (ID);

ALTER TABLE T_USER
  ADD CONSTRAINT u_name UNIQUE (USER_NAME);

ALTER TABLE T_USER
  ADD CONSTRAINT fk_upes FOREIGN KEY (PERSON_ID)
REFERENCES T_PERSON (ID);

ALTER TABLE T_BLOG
  ADD CONSTRAINT fk_buid FOREIGN KEY (USER_ID)
REFERENCES T_USER (ID);

ALTER TABLE T_BLOG_ENTRY
  ADD CONSTRAINT fk_bid FOREIGN KEY (BLOG_ID)
REFERENCES T_BLOG (ID);

ALTER TABLE T_GUEST_BOOK
  ADD CONSTRAINT fk_guid FOREIGN KEY (USER_ID)
REFERENCES T_USER (ID);

ALTER TABLE T_GUEST_BOOK_ENTRY
  ADD CONSTRAINT fk_geid FOREIGN KEY (GUEST_BOOK_ID)
REFERENCES T_GUEST_BOOK (ID);
