# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table checkpoint (
  id                        bigint auto_increment not null,
  name                      varchar(80) not null,
  longitude                 double not null,
  latitude                  double not null,
  points                    integer not null,
  message                   varchar(160) not null,
  scenario_id               bigint,
  checkpoint_index          integer not null,
  constraint pk_checkpoint primary key (id))
;

create table checkpoint_answer (
  id                        bigint auto_increment not null,
  checkpoint_id             bigint not null,
  text                      varchar(160) not null,
  constraint pk_checkpoint_answer primary key (id))
;

create table game (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  scenario_id               bigint,
  status                    varchar(7) not null,
  start_date                date,
  points_collected          integer,
  constraint ck_game_status check (status in ('playing','stopped','paused')),
  constraint pk_game primary key (id))
;

create table received_message (
  id                        bigint auto_increment not null,
  user_phone_number         varchar(9) not null,
  scenario_id               bigint,
  checkpoint_id             bigint,
  message                   varchar(255),
  constraint pk_received_message primary key (id))
;

create table scenario (
  id                        bigint auto_increment not null,
  name                      varchar(80) not null,
  is_public                 tinyint(1) default 0 not null,
  is_accepted               tinyint(1) default 0 not null,
  expiration_date           date,
  owner_id                  bigint,
  edited_by_id              bigint,
  constraint pk_scenario primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  email                     varchar(254) not null,
  alias                     varchar(20) not null,
  phone_number              varchar(9) not null,
  password_hash             varchar(60) not null,
  privilege                 varchar(7) not null,
  accepted_location         tinyint(1) default 0,
  constraint ck_user_privilege check (privilege in ('regular','admin')),
  constraint uq_user_email unique (email),
  constraint uq_user_alias unique (alias),
  constraint uq_user_phone_number unique (phone_number),
  constraint pk_user primary key (id))
;


create table game_sentcheckpoint (
  game_id                        bigint not null,
  checkpoint_id                  bigint not null,
  constraint pk_game_sentcheckpoint primary key (game_id, checkpoint_id))
;

create table game_answeredcheckpoint (
  game_id                        bigint not null,
  checkpoint_id                  bigint not null,
  constraint pk_game_answeredcheckpoint primary key (game_id, checkpoint_id))
;

create table scenario_user (
  scenario_id                    bigint not null,
  user_id                        bigint not null,
  constraint pk_scenario_user primary key (scenario_id, user_id))
;
alter table checkpoint add constraint fk_checkpoint_scenario_1 foreign key (scenario_id) references scenario (id) on delete restrict on update restrict;
create index ix_checkpoint_scenario_1 on checkpoint (scenario_id);
alter table checkpoint_answer add constraint fk_checkpoint_answer_checkpoint_2 foreign key (checkpoint_id) references checkpoint (id) on delete restrict on update restrict;
create index ix_checkpoint_answer_checkpoint_2 on checkpoint_answer (checkpoint_id);
alter table game add constraint fk_game_user_3 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_game_user_3 on game (user_id);
alter table game add constraint fk_game_scenario_4 foreign key (scenario_id) references scenario (id) on delete restrict on update restrict;
create index ix_game_scenario_4 on game (scenario_id);
alter table received_message add constraint fk_received_message_scenario_5 foreign key (scenario_id) references scenario (id) on delete restrict on update restrict;
create index ix_received_message_scenario_5 on received_message (scenario_id);
alter table received_message add constraint fk_received_message_checkpoint_6 foreign key (checkpoint_id) references checkpoint (id) on delete restrict on update restrict;
create index ix_received_message_checkpoint_6 on received_message (checkpoint_id);
alter table scenario add constraint fk_scenario_owner_7 foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_scenario_owner_7 on scenario (owner_id);
alter table scenario add constraint fk_scenario_editedBy_8 foreign key (edited_by_id) references user (id) on delete restrict on update restrict;
create index ix_scenario_editedBy_8 on scenario (edited_by_id);



alter table game_sentcheckpoint add constraint fk_game_sentcheckpoint_game_01 foreign key (game_id) references game (id) on delete restrict on update restrict;

alter table game_sentcheckpoint add constraint fk_game_sentcheckpoint_checkpoint_02 foreign key (checkpoint_id) references checkpoint (id) on delete restrict on update restrict;

alter table game_answeredcheckpoint add constraint fk_game_answeredcheckpoint_game_01 foreign key (game_id) references game (id) on delete restrict on update restrict;

alter table game_answeredcheckpoint add constraint fk_game_answeredcheckpoint_checkpoint_02 foreign key (checkpoint_id) references checkpoint (id) on delete restrict on update restrict;

alter table scenario_user add constraint fk_scenario_user_scenario_01 foreign key (scenario_id) references scenario (id) on delete restrict on update restrict;

alter table scenario_user add constraint fk_scenario_user_user_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table checkpoint;

drop table checkpoint_answer;

drop table game;

drop table game_sentcheckpoint;

drop table game_answeredcheckpoint;

drop table received_message;

drop table scenario;

drop table scenario_user;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

