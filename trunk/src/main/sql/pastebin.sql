-- Create the sequence needed by hibernate to create unique IDs.

CREATE SEQUENCE hibernate_sequence
        start 500 increment 1 maxvalue 9223372036854775807 minvalue 1 cache 1;


-- Create the table where pasted text is stored.

DROP TABLE pastebin_entry;

CREATE TABLE pastebin_entry (
    pastebin_entry_id int4 NOT NULL,
    parent_id int4 NULL,
    name varchar(255) NOT NULL,
    channel varchar(255) NOT NULL,
    code text NOT NULL,
    created timestamp NOT NULL,
    last_viewed timestamp NOT NULL,
    view_count int4,
    highlight varchar(255),
    private_pastebin_id int4,
    PRIMARY KEY(pastebin_entry_id)
);

-- modifications
ALTER TABLE pastebin_entry ADD COLUMN last_viewed timestamp;
ALTER TABLE pastebin_entry ADD COLUMN view_count int4;
ALTER TABLE pastebin_entry ADD COLUMN highlight varchar(255);
ALTER TABLE pastebin_entry ADD COLUMN private_pastebin_id int4;

-- Create the table where uploaded images are stored.

DROP TABLE image_entry;

CREATE TABLE image_entry (
    image_entry_id int4 NOT NULL,
    pastebin_entry_id int4 NULL,
    name varchar(255) NOT NULL,
    file_name varchar(255) NOT NULL,
    thumb_name varchar(255) NOT NULL,
    content_type varchar(100) NOT NULL,
    created timestamp NOT NULL,
    PRIMARY KEY(image_entry_id)
);


DROP TABLE private_pastebin;

CREATE TABLE private_pastebin (
    private_pastebin_id int4 NOT NULL,
    name varchar(255),
    email varchar(255),
    password varchar(255),
    created timestamp NOT NULL,
    last_used timestamp NOT NULL,
    PRIMARY KEY(private_pastebin_id)
);