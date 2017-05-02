create database curiosity;
use curiosity;

drop table if exists device_t;
create table device_t(
	id bigint primary key auto_increment,
    deviceid bigint unique not null,
	title varchar(256),
    private bool,
    feed varchar(256),
    status varchar(50),	-- live
    updated datetime,
    created datetime,
    email varchar(256),
    creator varchar(256),
    version varchar(50),
    tags text,
    description text,
    activity_daily_t varchar(256),    
    -- location
    disposition varchar(50), -- fix
    location_name varchar(50),    
    exposure varchar(50),
    domain varchar(50),
    ele varchar(50),
    lat double,
    lng double
);

drop table if exists datastream_t;
create table datastream_t(
	id bigint primary key auto_increment,
    deviceid bigint, 
    alias varchar(256),
    at datetime,
    current_value varchar(50),
    max_value varchar(50),
    min_value varchar(50),
    tags varchar(256),
    unit_symbol varchar(50),
    unit_label varchar(50),
    unit_type varchar(256),
    foreign key(deviceid) references device_t(deviceid)
);

create table datastream_stat_t(
	id bigint primary key,
	feedid bigint,
	datastreamid varchar(50),
	stat_type varchar(20), -- year, month, week, day
	time_at datetime,
	val varchar(50)
)

--drop table if exists datapoints_t;
--create table datapoints_t(
--	id bigint primary key auto_increment,
--    streamid bigint,
--    at datetime,
--    value varchar(256),    
--    foreign key(streamid) references datastream_t(id)
--);
--select * from device_t;
--delete from device_t;
