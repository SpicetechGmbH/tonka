-- liquibase formatted sql

-- changeset ma:6531b55a-60e4-4c56-b524-6761f6b17f3e
-- lemma_type definition

CREATE TABLE lemma_type (
  id VARCHAR(36) NOT NULL,
  lemma_type VARCHAR(1024) NOT NULL,
  lemma_type_gui_name VARCHAR(1024), 
  PRIMARY KEY (id));
  
-- changeset ma:98de8f03-7fd7-47fe-b757-36c31a001c41
-- lemma definition

CREATE TABLE lemma (
	id VARCHAR ( 36 ),
	lemma_type_id VARCHAR ( 36 ),
	timeline_date_day INT,
	timeline_date_month INT,
	timeline_date_year INT,
	timeline_date_label VARCHAR ( 1024 ),
	timeline_date_relevance VARCHAR ( 1024 ),
	timeline_title VARCHAR ( 1024 ),
	gnd_identifier VARCHAR ( 1024 ),
	featured BOOLEAN,
	PRIMARY KEY (id),
	CONSTRAINT lemma_lemma_type_id_fk FOREIGN KEY (lemma_type_id) REFERENCES lemma_type(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- changeset ma:ab9524d9-b39f-4916-858c-240e60af848f
-- lemma_version definition

CREATE TABLE lemma_version (
	id VARCHAR(36),
	lemma_id VARCHAR(36),
	version INTEGER,
	title VARCHAR(1024),
	abstract VARCHAR(1024),
	description VARCHAR(32672),
	lemma_reference VARCHAR(32672),
	lemma_literature VARCHAR(32672),
	author_name VARCHAR(1024),
	last_update DATE,
	citation VARCHAR(1024),
	PRIMARY KEY (id),
	CONSTRAINT lemma_version_lemma_id_fk FOREIGN KEY (lemma_id) REFERENCES lemma(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- changeset ma:71fc2af6-9695-493c-9323-9ceb8db57e85
-- keyword definition

CREATE TABLE keyword (
  id VARCHAR(36) NOT NULL,
  keyword VARCHAR(1024) NOT NULL,
  PRIMARY KEY (id));

CREATE UNIQUE INDEX keyword_unique_idx ON keyword (keyword ASC);

-- changeset ma:a8d608e3-5dc8-4253-bb40-66fe9ef12214
-- nm_lemma_keyword definition

CREATE TABLE nm_lemma_keyword (
  lemma_id VARCHAR(36) NOT NULL,
  keyword_id VARCHAR(36) NOT NULL,
  CONSTRAINT nm_lemma_keyword_lemma_id_fk FOREIGN KEY (lemma_id) REFERENCES lemma (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT nm_lemma_keyword_keyword_id_fk FOREIGN KEY (keyword_id) REFERENCES keyword (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- changeset ma:64e5bd46-01a5-4b67-b3b2-71c562953a01
-- illustration definition

CREATE TABLE illustration (
  id VARCHAR(36) NOT NULL,
  title VARCHAR(1024) NOT NULL,
  creator VARCHAR(1024),
  archive_signature VARCHAR(1024) NOT NULL,
  picture_date VARCHAR(1024),
  picture_description VARCHAR(1024),
  technique_material VARCHAR(1024),
  format_x_cm DECIMAL(31,2),
  format_y_cm DECIMAL(31,2),
  licence VARCHAR(1024) NOT NULL,
  source_base_information VARCHAR(32672),
  thumbnail_file_name VARCHAR(1024) NOT NULL,
  illustration_file_name VARCHAR(1024) NOT NULL,
  transcription_text VARCHAR(32672),
  repro VARCHAR(1024),
  PRIMARY KEY (ID)
);

-- changeset ma:62751f37-446c-497c-a449-69b2c3c87e41
-- nm_lemma_illustration definition

CREATE TABLE nm_lemma_illustration (
  lemma_id VARCHAR(36) NOT NULL,
  illustration_id VARCHAR(36) NOT NULL,
  nr INT,
	PRIMARY KEY (lemma_id, illustration_id),
	CONSTRAINT nm_lemma_illustration_lemma_id_fk FOREIGN KEY(lemma_id) REFERENCES lemma(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT nm_lemma_illustration_illustration_id_fk FOREIGN KEY(illustration_id) REFERENCES illustration(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- changeset ma:8b56b3e8-230b-4e77-ab10-bdc8a8281734
-- location definition

CREATE TABLE location (
        id VARCHAR (36) NOT NULL,
        internal_name VARCHAR (1024) NOT NULL,
        utm_coord_e INT NOT NULL,
        utm_coord_n INT NOT NULL,
        illustration_id VARCHAR(36),
        PRIMARY KEY (id)
    );

-- changeset ma:effd82a6-aaa6-4d32-ba9f-60ed6f7df09a
-- nm_lemma_location definition

CREATE TABLE nm_lemma_location (
	lemma_id VARCHAR(36) NOT NULL,
	location_id	VARCHAR(36) NOT NULL,
	location_relevance	VARCHAR(1024) NOT NULL,
	location_date_label	VARCHAR(1024),
	nr_of_location INT,
	main_location	INTEGER, 
	CONSTRAINT nm_lemma_location_lemma_id_fk FOREIGN KEY(lemma_id) REFERENCES lemma(id) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT nm_lemma_location_location_id_fk FOREIGN KEY(location_id) REFERENCES location(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- changeset ma:24f47f3a-dc06-4b37-899d-7d6894b8cd12
-- person definition

CREATE TABLE person (
	id VARCHAR(36) NOT NULL,
	lemma_id	VARCHAR(36) NOT NULL,
	full_name	VARCHAR(1024) NOT NULL,
	birth_place	VARCHAR(1024),
	death_place	VARCHAR(1024),
	birth_date	VARCHAR(1024),
	death_date	VARCHAR(1024),
	PRIMARY KEY(id),
	CONSTRAINT person_lemma_id_fk FOREIGN KEY(lemma_id) REFERENCES lemma(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- changeset ma:d1a2b45f-1417-4eae-a4cf-58e31fdee194
-- place definition

CREATE TABLE place (
	id	VARCHAR(36) NOT NULL,
	lemma_id	VARCHAR(36) NOT NULL,
	ADDRESS	VARCHAR(1024),
	PRIMARY KEY(id),
	CONSTRAINT place_lemma_id_fk FOREIGN KEY(lemma_id) REFERENCES lemma(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- changeset ma:b44a20b2-a128-4471-9d03-961cab430ecc
-- map_type definition

CREATE TABLE map_type (
  id VARCHAR(36) NOT NULL,
  map_type VARCHAR(1024) NOT NULL,
  PRIMARY KEY (id));

CREATE UNIQUE INDEX map_type_unique_idx ON map_type (map_type ASC);

-- changeset ma:2f8468da-b986-4845-9a97-7e406fb8dc79
-- map definition

CREATE TABLE map (
	id VARCHAR ( 36 ),
	map_type_id VARCHAR ( 36 ),
	url VARCHAR ( 1024 ),
	sublayer INT,
	timeline_date_day INT,
	timeline_date_month INT,
	timeline_date_year INT,
	timeline_date_label VARCHAR ( 1024 ),
	timeline_title VARCHAR ( 1024 ) DEFAULT (null),
	map_description VARCHAR ( 32672 ),
	archive_signature VARCHAR ( 1024 ),
	angle DECIMAL ( 31 , 2 ),
	x_min REAL,
	y_min REAL,
	x_max REAL,
	y_max REAL,
	PRIMARY KEY (id),
	CONSTRAINT map_map_type_id_fk FOREIGN KEY (map_type_id) REFERENCES map_type(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- changeset ma:44471935-018c-4fff-8741-f66e8772047a
-- street_map definition

CREATE TABLE street_map (
	id	VARCHAR(36),
	street_id	VARCHAR(36) NOT NULL UNIQUE,
	map_id	VARCHAR(36) NOT NULL,
	CONSTRAINT street_map_map_id_fk FOREIGN KEY(map_id) REFERENCES map(id) ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY(id)
);

-- changeset ma:381746df-e8c6-411e-95df-75bbd788cd3f
-- search_group definition

CREATE TABLE search_group (
	id	VARCHAR(36),
	name VARCHAR(255) NOT NULL,
	PRIMARY KEY(id)
);

-- changeset ma:99d0002b-e22e-457c-87d6-5321a2d8a12e
-- SEARCH_COLUMN definition

CREATE TABLE search_column (
	id	VARCHAR(36) NOT NULL,
	search_group_id	VARCHAR(36) NOT NULL,
	"column" VARCHAR(255) NOT NULL,
	quantifier	INTEGER NOT NULL,
	CONSTRAINT search_column_search_group_id_fk FOREIGN KEY(search_group_id) REFERENCES search_group(id) ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY(id)
);

-- changeset ma:08b4e65e-6aa4-4871-b3ed-e20cb9137525
-- spelling definition

CREATE TABLE spelling
(
	id VARCHAR(36) NOT NULL,
	primary_spelling VARCHAR(1024) NOT NULL,
	alternative_spelling VARCHAR(1024) NOT NULL UNIQUE,
	PRIMARY KEY(id)
);

-- changeset ma:34e475f3-4b1d-4e81-be21-8856507e0cee
-- start_message definition

CREATE TABLE start_message (
	id	VARCHAR(36),
	title	VARCHAR(1024) NOT NULL,
	"description"	TEXT NOT NULL,
	"begin"	DATE NOT NULL,
	"end"	DATE NOT NULL,
	PRIMARY KEY(id)
);

-- changeset ma:1c093b05-4c28-464e-9719-b30d42af83c6
-- view_lemma source

CREATE VIEW view_lemma as
select
	lemma.id
	, lemma.lemma_type_id
	, lemma_version.version
	, lemma_version.title
	, lemma_version.abstract
	, lemma_version.description
	, lemma_version.lemma_reference
	, lemma_version.lemma_literature
	, lemma.timeline_date_day
	, lemma.timeline_date_month
	, lemma.timeline_date_year
	, lemma.timeline_date_label
	, lemma.timeline_date_relevance
	, lemma.timeline_title
	, lemma_version.author_name
	, lemma.gnd_identifier
	, lemma.featured
	, lemma_version.last_update
	, lemma_version.citation
from
	lemma
inner join lemma_version on
	lemma.id = lemma_version.lemma_id;

-- changeset ma:d833f60e-5ad2-41c7-9198-988bba7ebb95
-- view_group2_search source

CREATE VIEW view_group2_search as
select
	lem.id
	, lemma_type
	, lemma_type_gui_name
	, max(lemma_version.version) as version
	, lemma_version.title
	, lemma_version.abstract
	, lemma_version.description
	, (
	select
		string_agg(keyword, ',') as keywords
	from
		lemma
	inner join nm_lemma_keyword on
		lemma.id = nm_lemma_keyword.lemma_id
	inner join keyword on
		nm_lemma_keyword.keyword_id = keyword.id
	where
		lemma.id = lem.id) as all_keywords
	, (
	select
		string_agg(distinct location_relevance, ',') as location_relevances
	from
		lemma
	inner join nm_lemma_location on
		lemma.id = nm_lemma_location.lemma_id
	inner join location on
		nm_lemma_location.location_id = location.id
	where
		lemma.id = lem.id) as all_location_relevances
	, lemma_version.author_name
	, lem.gnd_identifier
	, lemma_version.lemma_literature
	, lemma_version.lemma_reference
	, lem.timeline_title
	, lem.timeline_date_day
	, lem.timeline_date_month
	, lem.timeline_date_year
	, lem.timeline_date_label
	, lem.timeline_date_relevance
	, utm_coord_e
	, utm_coord_n
	, illustration.thumbnail_file_name
	, location_relevance
	, location_date_label
	, (
	select
		COALESCE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(title)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || COALESCE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(abstract)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || COALESCE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(description)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || COALESCE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(string_agg(DISTINCT keyword, ','))
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || COALESCE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(string_agg(DISTINCT location_relevance, ','))
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || COALESCE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(author_name)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || COALESCE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(gnd_identifier)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || COALESCE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(lemma_literature)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || COALESCE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(LOWER(lemma_reference)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '')
	from
		lemma
	inner join lemma_version on
		lemma.id = lemma_version.lemma_id
		and lemma_version.version = (select max(lemma_version.version) from lemma_version where lemma_version.lemma_id = lemma.id)
	left join nm_lemma_keyword on
		lemma.id = nm_lemma_keyword.lemma_id
	left join keyword on
		nm_lemma_keyword.keyword_id = keyword.id
	left join nm_lemma_location on
		lemma.id = nm_lemma_location.lemma_id
	left join location on
		nm_lemma_location.location_id = location.id
	where
		lemma.id = lem.id
	group by 
		title, abstract, description, author_name, gnd_identifier, lemma_literature, lemma_reference) as all_in_one
from
	lemma lem
inner join lemma_version on
	lem.id = lemma_version.lemma_id
	and lemma_version.version = (select max(lemma_version.version) from lemma_version where lemma_version.lemma_id = lem.id)
inner join lemma_type on
	lem.lemma_type_id = lemma_type.id
left join nm_lemma_location on
	lem.id = nm_lemma_location.lemma_id
	and nm_lemma_location.main_location = 1
left join location on
	nm_lemma_location.location_id = location.id
left join nm_lemma_illustration nli on
	lem.id = nli.lemma_id
	and nli.nr = 1
left join illustration on
	nli.illustration_id = illustration.id
group by
	lem.id,
	lemma_type,
	lemma_type_gui_name,
	lemma_version.title,
	lemma_version.abstract,
	lemma_version.description,
	lemma_version.author_name,
	lem.gnd_identifier,
	lemma_version.lemma_literature,
	lemma_version.lemma_reference,
	lem.timeline_title,
	lem.timeline_date_day,
	lem.timeline_date_month,
	lem.timeline_date_year,
	lem.timeline_date_label,
	lem.timeline_date_relevance,
	utm_coord_e,
	utm_coord_n,
	illustration.thumbnail_file_name,
	location_relevance,
	location_date_label;

	-- changeset ma:ccc5f141-c112-48b2-8c63-d0fe48c7d591
	-- view_group_illustration_2search source

CREATE VIEW view_group_illustration_2search as
SELECT
	ill.id
	, nli.lemma_id
	, lemma_type
	, nr
	, ill.title
	, timeline_title
	, lv.title AS lemma_title
	, thumbnail_file_name
	, creator
	, picture_description
	, licence
	, technique_material
	, archive_signature
	, transcription_text
	, (
	SELECT
		coalesce(replace(replace(replace(replace(replace(replace(replace(lower(archive_signature)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || coalesce(replace(replace(replace(replace(replace(replace(replace(lower(creator)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || coalesce(replace(replace(replace(replace(replace(replace(replace(lower(licence)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || coalesce(replace(replace(replace(replace(replace(replace(replace(lower(picture_description)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || coalesce(replace(replace(replace(replace(replace(replace(replace(lower(technique_material)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || coalesce(replace(replace(replace(replace(replace(replace(replace(lower(title)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || coalesce(replace(replace(replace(replace(replace(replace(replace(lower(transcription_text)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '')
	FROM
		illustration
	WHERE
		ill.id = illustration.id) as all_in_one
FROM
	illustration ill
LEFT JOIN nm_lemma_illustration nli ON
	ill.id = nli.illustration_id
LEFT JOIN lemma ON
	nli.lemma_id = lemma.id
LEFT JOIN lemma_type ON
	lemma.lemma_type_id = lemma_type.id
LEFT JOIN lemma_version lv ON
	nli.lemma_id = lv.lemma_id
	AND lv.version = (
	SELECT
		MAX(lv2.version)
	FROM
		lemma_version lv2
	WHERE
		lv2.lemma_id = nli.lemma_id);

-- changeset ma:5e7b259c-346f-468b-88e6-1c3f41918efa
-- view_group_bionet_2search source

CREATE VIEW view_group_bionet_2search as
SELECT
	loc.id
	, nm_lemma_location.lemma_id
	, internal_name
	, location_relevance
	, title
	, timeline_title
	, lemma_type
	, (
	SELECT
		coalesce(replace(replace(replace(replace(replace(replace(replace(lower(internal_name)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '') || ' ' || coalesce(replace(replace(replace(replace(replace(replace(replace(lower(location_relevance)
		, '&#xc4;', 'ä')
		, '&#xe4;', 'ä')
		, '&#xd6;', 'ö')
		, '&#xf6;', 'ö')
		, '&#xdc;', 'ü')
		, '&#xfc;', 'ü')
		, '&#x2013;', '-')
		, '')
	FROM
		location
	WHERE
		loc.id = location.id) as all_in_one
FROM
	LOCATION loc
LEFT JOIN nm_lemma_location ON
	loc.id = nm_lemma_location.location_id
LEFT JOIN lemma ON
	nm_lemma_location.lemma_id = lemma.id
LEFT JOIN lemma_version lv ON
	nm_lemma_location.lemma_id = lv.lemma_id
	AND lv.version = (
	SELECT
		max(lv2.version)
	FROM
		lemma_version lv2
	WHERE
		lv2.lemma_id = nm_lemma_location.lemma_id)
LEFT JOIN lemma_type ON
	lemma.lemma_type_id = lemma_type.id;

-- changeset ma:91c729e4-d186-4210-af1f-7ebeb401b668
ALTER TABLE nm_lemma_illustration ADD CONSTRAINT nm_lemma_illustration_lemma_id_nr_unique UNIQUE (lemma_id,nr);

-- changeset ma:0333900a-a976-4bc6-bf23-ee1cdc1ddbd3
ALTER TABLE "map" ADD layer varchar NULL;
COMMENT ON COLUMN "map".layer IS 'Contains the name of one or multiple layers for wms map, multiple layers are separated by comma without any space around.';

-- changeset ma:7147ea25-27aa-44b2-a586-bef7e51fac6e
ALTER TABLE "map" ADD service varchar(255) NULL;
COMMENT ON COLUMN "map".service IS 'Provisioning service for the map.';

-- changeset ma:1149af41-8c57-447c-acc1-7e9a1c31b8e5
ALTER TABLE lemma ADD link varchar(255) NULL;
COMMENT ON COLUMN lemma.link IS 'textual link to lemma';
ALTER TABLE lemma ADD CONSTRAINT lemma_link_unique UNIQUE (link);

-- changeset ma:84104c0e-fd01-4b98-84fc-d0308c8a714b
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE lemma_sync (
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	lemma_version_id varchar(36) NOT NULL,
	audio_data bytea NOT NULL,
	file_format varchar(10) DEFAULT 'wav' NOT NULL,
	created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT lemma_sync_pk PRIMARY KEY (id),
	CONSTRAINT lemma_sync_lemma_version_fk FOREIGN KEY (lemma_version_id) REFERENCES lemma_version(id) ON DELETE CASCADE ON UPDATE CASCADE
);
COMMENT ON TABLE lemma_sync IS 'Synchronisation files for lemmas.';
COMMENT ON COLUMN lemma_sync.created_at IS 'Creation time of entry.';





INSERT INTO lemma_type (id,lemma_type,lemma_type_gui_name) VALUES
	 ('9588d70d-f658-423b-8699-4fe863e9797a','PERSON','Personen'),
	 ('4017f08c-bba3-4f35-9650-5342e141bf44','EVENT','Ereignisse'),
	 ('aa262d9d-5c16-480b-8126-704752bb1cc5','PLACE','Orte'),
	 ('7772dc5d-003f-4cbf-8eb2-88b02870d4a8','TOPIC','Themen'),
	 ('a4793cad-55da-4489-b263-c64ff806a262','INSTITUTION','Institutionen');

INSERT INTO map_type (id,map_type) VALUES
	 ('42fd5ee6-208e-41fb-8342-4b2300d23d66','BASEMAP'),
	 ('d0c115e1-c47a-4f89-a65b-61009b7232f2','HISTORICMAP'),
	 ('d7be331c-8ac6-11ea-bc55-0242ac130003','THEMATICMAP');

INSERT INTO search_group (id,name) VALUES
	 ('ce6a9ee6-6ed2-11ea-bc55-0242ac130003','map'),
	 ('ce6aa120-6ed2-11ea-bc55-0242ac130003','illustration'),
	 ('ce6aa210-6ed2-11ea-bc55-0242ac130003','biographic_net'),
	 ('7e711568-736a-11ea-bc55-0242ac130003','lemma');

INSERT INTO search_column (id,search_group_id,"column",quantifier) VALUES
	 ('9df862d8-6ed3-11ea-bc55-0242ac130003','ce6a9ee6-6ed2-11ea-bc55-0242ac130003','MAP_DESCRIPTION',1000),
	 ('9df86508-6ed3-11ea-bc55-0242ac130003','ce6a9ee6-6ed2-11ea-bc55-0242ac130003','ARCHIVE_SIGNATURE',100),
	 ('d740fe60-6f82-11ea-bc55-0242ac130003','ce6aa120-6ed2-11ea-bc55-0242ac130003','TITLE',10000),
	 ('d7410072-6f82-11ea-bc55-0242ac130003','ce6aa120-6ed2-11ea-bc55-0242ac130003','CREATOR',1000),
	 ('d741016c-6f82-11ea-bc55-0242ac130003','ce6aa120-6ed2-11ea-bc55-0242ac130003','PICTURE_DESCRIPTION',1000),
	 ('d741023e-6f82-11ea-bc55-0242ac130003','ce6aa120-6ed2-11ea-bc55-0242ac130003','LICENCE',10),
	 ('d7410310-6f82-11ea-bc55-0242ac130003','ce6aa120-6ed2-11ea-bc55-0242ac130003','TECHNIQUE_MATERIAL',10),
	 ('d74104aa-6f82-11ea-bc55-0242ac130003','ce6aa120-6ed2-11ea-bc55-0242ac130003','ARCHIVE_SIGNATURE',100),
	 ('3a305d92-7021-11ea-bc55-0242ac130003','ce6aa210-6ed2-11ea-bc55-0242ac130003','INTERNAL_NAME',100),
	 ('3a3060f8-7021-11ea-bc55-0242ac130003','ce6aa210-6ed2-11ea-bc55-0242ac130003','LOCATION_RELEVANCE',100),
	 ('0fdac706-736b-11ea-bc55-0242ac130003','7e711568-736a-11ea-bc55-0242ac130003','TITLE',1000000),
	 ('0fdac972-736b-11ea-bc55-0242ac130003','7e711568-736a-11ea-bc55-0242ac130003','ABSTRACT',10000),
	 ('0fdaca76-736b-11ea-bc55-0242ac130003','7e711568-736a-11ea-bc55-0242ac130003','DESCRIPTION',5000),
	 ('0fdacb52-736b-11ea-bc55-0242ac130003','7e711568-736a-11ea-bc55-0242ac130003','ALL_KEYWORDS',1000),
	 ('0fdacc24-736b-11ea-bc55-0242ac130003','7e711568-736a-11ea-bc55-0242ac130003','ALL_LOCATION_RELEVANCES',600),
	 ('0fdaccf6-736b-11ea-bc55-0242ac130003','7e711568-736a-11ea-bc55-0242ac130003','AUTHOR_NAME',500),
	 ('0fdaced6-736b-11ea-bc55-0242ac130003','7e711568-736a-11ea-bc55-0242ac130003','GND_IDENTIFIER',400),
	 ('0fdacfb2-736b-11ea-bc55-0242ac130003','7e711568-736a-11ea-bc55-0242ac130003','LEMMA_LITERATURE',300),
	 ('0fdad084-736b-11ea-bc55-0242ac130003','7e711568-736a-11ea-bc55-0242ac130003','LEMMA_REFERENCE',200),
	 ('d7410572-6f82-11ea-bc55-0242ac130003','ce6aa120-6ed2-11ea-bc55-0242ac130003','TRANSCRIPTION_TEXT',100);

INSERT INTO start_message(id, title, "description", "begin", "end") VALUES
   ('4313e7db-010e-467c-8a75-f5d5516b65ec', 'Startseite', 'Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat. In id cursus mi pretium tellus duis convallis. Tempus leo eu aenean sed diam urna tempor. Pulvinar vivamus fringilla lacus nec metus bibendum egestas. Iaculis massa nisl malesuada lacinia integer nunc posuere. Ut hendrerit semper vel class aptent taciti sociosqu. Ad litora torquent per conubia nostra inceptos himenaeos.

<a href="https://www.spicetech.de" target="_blank">Beispiellink</a>', '2023-01-01', '2099-12-31');

INSERT INTO lemma (id,lemma_type_id,timeline_date_day,timeline_date_month,timeline_date_year,timeline_date_label,timeline_date_relevance,timeline_title,gnd_identifier,featured,link) VALUES
	 ('3b8772f9-61b3-49b9-b7c1-920e3c5e6ea6','a4793cad-55da-4489-b263-c64ff806a262',15,5,2005,'5. Mai 2005','Das Stadtarchiv der Stadt Stuttgart','Stadtarchiv',NULL,NULL,NULL),
	 ('3ed9c3ef-9f4b-4d67-a38d-b745a665b9e7','a4793cad-55da-4489-b263-c64ff806a262',11,1,2011,'1. Januar 2011','Das Stadtmessungsamt der Stadt Stuttgart','Stadtmessungsamt',NULL,NULL,NULL),
	 ('d9ea60eb-856c-45c5-be3d-92816ac307fd','aa262d9d-5c16-480b-8126-704752bb1cc5',15,10,2016,'15. Oktober 2016','Die Entwickler des Stadtlexikons','Spicetech',NULL,NULL,NULL);

INSERT INTO lemma_version (id,lemma_id,"version",title,abstract,description,lemma_reference,lemma_literature,author_name,last_update,citation) VALUES
	 ('067847bf-4fd1-48ab-a421-fb2ae6336f56','3b8772f9-61b3-49b9-b7c1-920e3c5e6ea6',1,'Das Stadtarchiv Stuttgart: Hüterin der Stadtgeschichte','Das Stadtarchiv Stuttgart ist das zentrale Gedächtnis der Landeshauptstadt und dient als Kompetenzzentrum für die Stadtgeschichte. Es bewahrt wertvolle historische Quellen auf, sichert Bürgerrechte und unterstützt eine transparente Verwaltung. Mit umfangreichen Beständen, einem breiten Veranstaltungsangebot und dem innovativen Stadtlexikon richtet sich das Archiv gleichermaßen an Wissenschaft, Öffentlichkeit und Verwaltung.','Das Stadtarchiv Stuttgart übernimmt eine bedeutende Rolle als Bewahrerin der Geschichte der Landeshauptstadt. Es versteht sich nicht nur als Archiv im klassischen Sinne, sondern als aktives Bürgerarchiv, das authentische Zeugnisse vergangener Zeiten sichert und zugänglich macht. Dazu gehören Akten, Amtsbücher, Fotografien, Karten, Filme, Gemälde und Grafiken sowohl amtlicher als auch privater Herkunft.
Als Kompetenzzentrum für Stadtgeschichte bietet das Stadtarchiv umfangreiche Serviceleistungen. Bürgerinnen und Bürger sowie Forschende haben die Möglichkeit, im Lesesaal Dokumente einzusehen. Dort stehen moderne Arbeitsplätze, Mikrofilmgeräte sowie Buchscanner zur Verfügung. Darüber hinaus ist die Nutzung der 51.000 Medieneinheiten umfassenden Präsenzbibliothek möglich, die auf Stadt- und Landesgeschichte sowie Archivwesen spezialisiert ist.
Das Stadtarchiv fördert aktiv die Vermittlung von Geschichtswissen. Mit Vorträgen, Seminaren, Workshops und Tagungen werden historische Themen in aktuelle gesellschaftliche Kontexte gestellt. Gleichzeitig bietet der Blog „Archiv0711“ sowie die Präsenz auf sozialen Medien spannende Einblicke in die Arbeit des Archivs und stellt aktuelle Forschungsergebnisse sowie interessante Stadtgeschichten vor.
Besonders nutzerfreundlich ist der Zugang zu den Beständen: Über den Online-Katalog können zahlreiche Dokumente recherchiert und zur Einsicht in den Lesesaal bestellt werden. Auch wichtige Personenstandsunterlagen wie Geburts-, Heirats- und Sterberegister können eingesehen oder online recherchiert werden.
Ein besonderes Highlight des Stadtarchivs ist das digitale Stadtlexikon Stuttgart, das gemeinsam mit dem Stadtmessungsamt entwickelt wurde. Dieses Stadtlexikon basiert exakt auf der Software, die auch in dieser Unterhaltung verwendet wird. Die Software wurde durch die Spicetech GmbH mit Sitz in Stuttgart entwickelt. Das digitale Stadtlexikon ermöglicht eine ortsunabhängige und intuitive Erkundung der Stadtgeschichte: Nutzerinnen und Nutzer finden fundierte Informationen zu bedeutenden Orten, Persönlichkeiten und historischen Ereignissen. Zudem werden historische Karten und Archivdokumente ansprechend aufbereitet und interaktiv zugänglich gemacht, etwa über Kartenvergleiche zwischen damals und heute. Bereits kurz nach dem Start wurde das Stadtlexikon für den renommierten Grimme-Online-Award nominiert – ein Zeichen für die hohe Qualität und Benutzerfreundlichkeit der Plattform.
Mit seinem umfassenden Angebot leistet das Stadtarchiv einen unverzichtbaren Beitrag zur historischen Forschung, Wahrung städtischer Identität und Förderung einer transparenten Stadtverwaltung. Das digitale Stadtlexikon ergänzt diese Arbeit auf moderne und innovative Weise und macht Stadtgeschichte für alle erlebbar – ob zuhause oder unterwegs.',NULL,NULL,'Chat GPT','2025-04-29',NULL),
	 ('1b1b84df-a113-4f93-b90a-43a255218eb4','3ed9c3ef-9f4b-4d67-a38d-b745a665b9e7',1,'Das Stadtmessungsamt Stuttgart: Geodaten für eine nachhaltige Stadtentwicklung','Das Stadtmessungsamt der Stadt Stuttgart ist eine zentrale Einrichtung für die Verwaltung, Erfassung und Bereitstellung von Geodaten. Es spielt eine entscheidende Rolle bei der nachhaltigen Entwicklung der Landeshauptstadt. Mit einem umfassenden GeoService, modernen Geoinformationssystemen (GIS) und einer Vielzahl an Serviceleistungen unterstützt das Amt die Stadtverwaltung, Wirtschaft und Öffentlichkeit gleichermaßen. Der Artikel gibt einen Überblick über die Aufgaben, die Bedeutung und die Struktur des Stadtmessungsamtes.','Geodaten als Fundament städtischer Entscheidungen
In einer wachsenden und dynamischen Stadt wie Stuttgart sind fundierte Informationen über Grund und Boden von unschätzbarem Wert. Nahezu jede kommunale Entscheidung – sei es der Bau von Wohnhäusern, die Planung neuer Verkehrswege oder die Gestaltung von Erholungsflächen – erfordert aktuelle und präzise Flächendaten. Hier tritt das Stadtmessungsamt als unverzichtbarer Partner auf: Es stellt die grundlegenden Kartenwerke und digitalen Geoinformationen bereit, die Planungen und Projekte erst möglich machen. Trotz der Digitalisierung bleibt auch die Nachfrage nach klassischen, gedruckten Karten ungebrochen hoch.
Der GeoService als Motor für Stadtentwicklung
Das Stadtmessungsamt versteht sich als aktiver Unterstützer der städtischen Entwicklung. Unter dem Leitmotiv, die Weiterentwicklung Stuttgarts zu fördern, bietet der GeoService eine breite Palette an Dienstleistungen und Produkten für Verwaltung, Wirtschaft und Bürgerinnen und Bürger an. Im Zentrum steht die fortlaufende Erhebung und Aktualisierung raumbezogener Daten, die für eine Vielzahl von Aufgaben – von der Stadt- und Regionalplanung über den Umweltschutz bis hin zur Wirtschaftsförderung – von zentraler Bedeutung sind.
Ein besonderes Augenmerk liegt auf dem Einsatz moderner Geoinformationssysteme (GIS). Diese ermöglichen es, raumbezogene Daten nicht nur zu speichern, sondern auch anschaulich zu analysieren und zu visualisieren. Die Informationen werden sowohl über das Internet als auch über interne städtische Plattformen bereitgestellt und unterstützen somit zahlreiche Fachbereiche innerhalb und außerhalb der Stadtverwaltung.
Ein Servicezentrum für Bürger und Wirtschaft
Das Stadtmessungsamt ist verkehrsgünstig in unmittelbarer Nähe zum Hauptbahnhof angesiedelt und in vier Fachabteilungen untergliedert. Diese Struktur erlaubt es, auf unterschiedlichste Anliegen kompetent und effizient einzugehen. Zum Leistungsspektrum gehören unter anderem die Erteilung von Auskünften zu Grundstücksfragen und die Einsichtnahme in das städtische Grundbuch.
Besonders hervorzuheben ist die Geschäftsstelle des Gutachterausschusses, die innerhalb des Stadtmessungsamtes angesiedelt ist. Sie ist verantwortlich für die Ermittlung von Grundstückswerten und stellt wichtige Daten für Immobilienmarktanalysen bereit. Das Kundenzentrum des Stadtmessungsamtes fungiert als zentrale Anlaufstelle und unterstützt sowohl private Bauherren als auch Unternehmen und Behörden bei ihren Vorhaben.',NULL,NULL,'Chat GPT','2025-04-29',NULL),
	 ('743381be-2683-4d79-8485-5d3f032c40c0','d9ea60eb-856c-45c5-be3d-92816ac307fd',1,'Spicetech GmbH: Digitale Innovationen mit Würze','Die Spicetech GmbH ist ein IT-Dienstleister mit Sitz in Stuttgart, der sich auf die Entwicklung und den Betrieb digitaler Product-as-a-Service-Lösungen spezialisiert hat. Das Unternehmen bietet maßgeschneiderte Software- und KI-Produkte für Branchen wie Energie, Lebensmittel & Getränke sowie Wissenschaft an. Mit innovativen Tools wie PREDECY, QUANTICY, VALICY und GENERICY unterstützt Spicetech Unternehmen bei der Digitalisierung und Automatisierung ihrer Prozesse.','Die Spicetech GmbH, gegründet im Jahr 2016, hat sich als innovativer IT-Dienstleister etabliert, der Unternehmen bei der digitalen Transformation unterstützt. Mit einem Team von 16 Experten entwickelt und betreibt Spicetech individuelle digitale Product-as-a-Service-Lösungen, die speziell auf die Bedürfnisse ihrer Kunden zugeschnitten sind.
Das Unternehmen bedient verschiedene Branchen, darunter Energie, Lebensmittel & Getränke sowie Wissenschaft.Insgesamt wurden bereits 34 kundenindividuelle Lösungen umgesetzt, davon 14 im Energiesektor, 8 im Lebensmittel- und Getränkesektor und 9 in Wissenschaft und öffentlichem Sektor.
Zu den Kernprodukten von Spicetech gehören:
PREDECY: Ein KI-basiertes Tool zur Absatzprognose, das Unternehmen hilft, ihre Verkaufszahlen genauer vorherzusagen und ihre Planung zu optimieren.
QUANTICY: Ein Planungsinstrument für Ladeinfrastruktur, das optimale Standorte für Investitionen identifiziert.
VALICY: Ein Tool zur Prüfung und Absicherung eigener KI-Systeme, das deren Funktionsgrenzen analysiert.
GENERICY: Ein Werkzeug, das SQL-Datenbankabfragen in leistungsstarke REST-APIs umwandelt, um Datenprozesse zu automatisieren.
Spicetech legt großen Wert auf kreative Projektnamen mit Gewürzbezug, wie LEMON, CAJUN oder CURRY, die die Individualität und den innovativen Ansatz des Unternehmens unterstreichen.
Mit über 1,8 Millionen eingesetzten KI-Modellen pro Jahr demonstriert Spicetech seine Kompetenz in der Anwendung künstlicher Intelligenz zur Prozessoptimierung. Das Unternehmen bietet seine Lösungen sowohl als individuelle Implementierungen als auch als Lizenzmodelle an, um den unterschiedlichen Anforderungen seiner Kunden gerecht zu werden.
Für weitere Informationen besuchen Sie die Website der Spicetech GmbH unter www.spicetech.de.
Die Spicetech GmbH ist auf die Konzeption, Entwicklung und den Betrieb von Software und AI-as-a-Service Systemen spezialisiert.
 ',NULL,NULL,'Chat GPT','2025-04-29',NULL),
	 ('5df797bc-f059-4227-a8e0-df64067813c7','d9ea60eb-856c-45c5-be3d-92816ac307fd',2,'Spicetech GmbH: Digitale Innovationen mit Würze','Die Spicetech GmbH ist ein IT-Dienstleister mit Sitz in Stuttgart, der sich auf die Entwicklung und den Betrieb digitaler Product-as-a-Service-Lösungen spezialisiert hat. Das Unternehmen bietet maßgeschneiderte Software- und KI-Produkte für Branchen wie Energie, Lebensmittel & Getränke sowie Wissenschaft an. Mit innovativen Tools wie PREDECY, QUANTICY, VALICY und GENERICY unterstützt Spicetech Unternehmen bei der Digitalisierung und Automatisierung ihrer Prozesse.','Die Spicetech GmbH, gegründet im Jahr 2016, hat sich als innovativer IT-Dienstleister etabliert, der Unternehmen bei der digitalen Transformation unterstützt. Mit einem Team von 16 Experten entwickelt und betreibt Spicetech individuelle digitale Product-as-a-Service-Lösungen, die speziell auf die Bedürfnisse ihrer Kunden zugeschnitten sind.
Das Unternehmen bedient verschiedene Branchen, darunter Energie, Lebensmittel & Getränke sowie Wissenschaft.Insgesamt wurden bereits 34 kundenindividuelle Lösungen umgesetzt, davon 14 im Energiesektor, 8 im Lebensmittel- und Getränkesektor und 9 in Wissenschaft und öffentlichem Sektor.
Zu den Kernprodukten von Spicetech gehören:
PREDECY: Ein KI-basiertes Tool zur Absatzprognose, das Unternehmen hilft, ihre Verkaufszahlen genauer vorherzusagen und ihre Planung zu optimieren.
QUANTICY: Ein Planungsinstrument für Ladeinfrastruktur, das optimale Standorte für Investitionen identifiziert.
VALICY: Ein Tool zur Prüfung und Absicherung eigener KI-Systeme, das deren Funktionsgrenzen analysiert.
GENERICY: Ein Werkzeug, das SQL-Datenbankabfragen in leistungsstarke REST-APIs umwandelt, um Datenprozesse zu automatisieren.
Spicetech legt großen Wert auf kreative Projektnamen mit Gewürzbezug, wie LEMON, CAJUN oder CURRY, die die Individualität und den innovativen Ansatz des Unternehmens unterstreichen.
Mit über 1,8 Millionen eingesetzten KI-Modellen pro Jahr demonstriert Spicetech seine Kompetenz in der Anwendung künstlicher Intelligenz zur Prozessoptimierung. Das Unternehmen bietet seine Lösungen sowohl als individuelle Implementierungen als auch als Lizenzmodelle an, um den unterschiedlichen Anforderungen seiner Kunden gerecht zu werden.
Für weitere Informationen besuchen Sie die Website der Spicetech GmbH unter www.spicetech.de.
Die Spicetech GmbH ist auf die Konzeption, Entwicklung und den Betrieb von Software und AI-as-a-Service Systemen spezialisiert.
 ',NULL,NULL,'Chat GPT','2025-04-29',NULL);

INSERT INTO "location" (id,internal_name,utm_coord_e,utm_coord_n,illustration_id) VALUES
	 ('a8beb201-b33f-41f2-baf3-29967d7e5fcd','Spicetech I',512097,5402670,NULL),
	 ('c8261256-e2ad-41de-a56d-86217779aad9','Spicetech II',513776,5402516,NULL),
	 ('b015e2ab-300d-4b5b-868d-ceed76032d9b','Stadtarchiv',516538,5404979,NULL),
	 ('ed89f05f-8bfc-421d-ac4c-23e4dd8c24c2','Stadtmessungsamt',513142,5403321,NULL);

INSERT INTO illustration (id,title,creator,archive_signature,picture_date,picture_description,technique_material,format_x_cm,format_y_cm,licence,source_base_information,thumbnail_file_name,illustration_file_name,transcription_text,repro) VALUES
	 ('530459b3-0b97-444f-9ed0-a8a6da4628b8','Stadtarchiv Aufnahmetisch','Chat GPT','ST-20250248-005','28. April 2025','Fotobereich im Stadtarchiv','Gemälde',NULL,NULL,'CC',NULL,'stadtarchiv_gemaelde_1.thumbnail.jpg','stadtarchiv_gemaelde_1.png',NULL,NULL),
	 ('d67b2b94-b7b0-446a-a018-5102e6fec5fd','Stadtarchiv Anzeigegeraet','Chat GPT','ST-20250248-004','28. April 2025','Anzeigegerät im Stadtarchiv','Gemälde',NULL,NULL,'CC',NULL,'stadtarchiv_gemaelde_2.thumbnail.jpg','stadtarchiv_gemaelde_2.png',NULL,NULL),
	 ('ea352071-8d7c-41d7-a07e-30db0d3a9f80','Stadtarchiv Lesesaal','Chat GPT','ST-20250248-003','28. April 2025','Lesesaal im Stadtarchiv','Gemälde',NULL,NULL,'CC',NULL,'stadtarchiv_gemaelde_3.thumbnail.jpg','stadtarchiv_gemaelde_3.png',NULL,NULL),
	 ('200c84a6-33d9-43b2-a5a3-50b232748ee6','Stadtmessungsamt Gebäude hell','Chat GPT','ST-20250248-002','28. April 2025','Gebäude des Stadtmessungsamt','Gemälde',NULL,NULL,'CC',NULL,'stadtmessungsamt_gemaelde_1.thumbnail.jpg','stadtmessungsamt_gemaelde_1.png',NULL,NULL),
	 ('48fbaa55-3bae-458b-947f-ee55202315d7','Spicetech - Digitale Innovationen mit Würze','Chat GPT','ST-20250248-001','28. April 2025','Spicetech Innovationslogo als Gemälde','Gemälde',NULL,NULL,'CC',NULL,'spicetech_gemaelde_1.thumbnail.jpg','spicetech_gemaelde_1.png',NULL,NULL);

INSERT INTO nm_lemma_location (lemma_id,location_id,location_relevance,location_date_label,nr_of_location,main_location) VALUES
	 ('d9ea60eb-856c-45c5-be3d-92816ac307fd','a8beb201-b33f-41f2-baf3-29967d7e5fcd','alter Standort','bis 2024',1,NULL),
	 ('d9ea60eb-856c-45c5-be3d-92816ac307fd','c8261256-e2ad-41de-a56d-86217779aad9','aktueller Standort seit 2025','2025',2,1),
	 ('3b8772f9-61b3-49b9-b7c1-920e3c5e6ea6','b015e2ab-300d-4b5b-868d-ceed76032d9b','Standort',NULL,NULL,1),
	 ('3ed9c3ef-9f4b-4d67-a38d-b745a665b9e7','ed89f05f-8bfc-421d-ac4c-23e4dd8c24c2','Standort',NULL,NULL,1);

INSERT INTO nm_lemma_illustration (lemma_id,illustration_id,nr) VALUES
	 ('d9ea60eb-856c-45c5-be3d-92816ac307fd','48fbaa55-3bae-458b-947f-ee55202315d7',1),
	 ('3ed9c3ef-9f4b-4d67-a38d-b745a665b9e7','200c84a6-33d9-43b2-a5a3-50b232748ee6',1),
	 ('3b8772f9-61b3-49b9-b7c1-920e3c5e6ea6','530459b3-0b97-444f-9ed0-a8a6da4628b8',1),
	 ('3b8772f9-61b3-49b9-b7c1-920e3c5e6ea6','d67b2b94-b7b0-446a-a018-5102e6fec5fd',2),
	 ('3b8772f9-61b3-49b9-b7c1-920e3c5e6ea6','ea352071-8d7c-41d7-a07e-30db0d3a9f80',3);
