drop schema if exists chatdemo;
create schema chatdemo;
use chatdemo;

create table messages(
    message_id int auto_increment not null,
    chat_id varchar(45),
    sender varchar(45),
    content varchar(256),
    msg_timestamp timestamp,
    primary key(message_id)
);