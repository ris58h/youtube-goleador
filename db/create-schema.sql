CREATE TABLE video (
  id bigserial PRIMARY KEY,
  video_id varchar(20) UNIQUE NOT NULL,
  times varchar(255),
  error varchar(255),
  comment_id varchar(50) UNIQUE
);

CREATE TABLE channel (
  id bigserial PRIMARY KEY,
  channel_id varchar(30) UNIQUE NOT NULL,
  since bigint
);