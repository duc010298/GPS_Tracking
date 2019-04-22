SET timezone TO 'UTC';
CREATE extension IF NOT EXISTS "uuid-ossp";
-- user manager
CREATE TABLE IF NOT EXISTS app_user
(
	user_id		uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
	user_name 	varchar(36)	NOT NULL UNIQUE,
	encrypted_password		varchar(128) NOT NULL,
	token_active_after		timestamp NOT NULL DEFAULT now()
)
CREATE TABLE IF NOT EXISTS app_role
(
  role_id   bigint      NOT NULL PRIMARY KEY,
  role_name varchar(30) NOT NULL
);
CREATE TABLE IF NOT EXISTS user_role
(
  user_id uuid NOT NULL,
  role_id bigint NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT user_role_fk1 FOREIGN KEY (user_id) REFERENCES app_user (user_id),
  CONSTRAINT user_role_fk2 FOREIGN KEY (role_id) REFERENCES app_role (role_id)
);
--
INSERT INTO app_user (user_id, user_name, encrypted_password, token_active_after)
VALUES ('44260380-cc98-4296-acac-1d57b88b2535', 'duc010298', '$2a$10$hjBz774Yg4Fff44DYseK4.w4p27w2enR0W.QxSxlIXA.TcxS2bYV.', DEFAULT);
--
INSERT INTO app_role (role_id, role_name)
VALUES (1, 'ROLE_ADMIN');

INSERT INTO app_role (role_id, role_name)
VALUES (2, 'ROLE_MEMBER');
--
INSERT INTO user_role
VALUES ('44260380-cc98-4296-acac-1d57b88b2535', 1);

INSERT INTO user_role
VALUES ('44260380-cc98-4296-acac-1d57b88b2535', 2);
--
CREATE TABLE IF NOT EXISTS device
(
	imei char(15) PRIMARY KEY,
	user_id uuid NOT null,
	device_name varchar(100) NOT NULL,
	is_online BOOLEAN,
	last_online timestamp,
	CONSTRAINT device_fk1 FOREIGN KEY (user_id) REFERENCES app_user (user_id)
);

CREATE TABLE IF NOT EXISTS location_history
(
	location_id 	uuid DEFAULT uuid_generate_v4() PRIMARY key,
	imei 			char(15) NOT NULL,
	time_tracking 	timestamp,
	latitude 		float,
	longitude		float,
	CONSTRAINT location_history_fk1 FOREIGN KEY (imei) REFERENCES device (imei)
);

