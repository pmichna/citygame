# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table checkpoint (
  id                        bigint not null,
  scenario_id               bigint not null,
  constraint pk_checkpoint primary key (id))
;

create table scenario (
  id                        bigint not null,
  name                      varchar(255),
  is_public                 boolean,
  is_accepted               boolean,
  expiration_date           timestamp,
  owner_email               varchar(255),
  constraint pk_scenario primary key (id))
;

create table user (
  email                     varchar(255) not null,
  alias                     varchar(255),
  password_hashed           varchar(255),
  constraint pk_user primary key (email))
;


create table scenario_user (
  scenario_id                    bigint not null,
  user_email                     varchar(255) not null,
  constraint pk_scenario_user primary key (scenario_id, user_email))
;
create sequence checkpoint_seq;

create sequence scenario_seq;

create sequence user_seq;

alter table checkpoint add constraint fk_checkpoint_scenario_1 foreign key (scenario_id) references scenario (id) on delete restrict on update restrict;
create index ix_checkpoint_scenario_1 on checkpoint (scenario_id);
alter table scenario add constraint fk_scenario_owner_2 foreign key (owner_email) references user (email) on delete restrict on update restrict;
create index ix_scenario_owner_2 on scenario (owner_email);



alter table scenario_user add constraint fk_scenario_user_scenario_01 foreign key (scenario_id) references scenario (id) on delete restrict on update restrict;

alter table scenario_user add constraint fk_scenario_user_user_02 foreign key (user_email) references user (email) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists checkpoint;

drop table if exists scenario;

drop table if exists scenario_user;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists checkpoint_seq;

drop sequence if exists scenario_seq;

drop sequence if exists user_seq;

