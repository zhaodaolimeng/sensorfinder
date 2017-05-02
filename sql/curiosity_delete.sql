delete from datapoints_t where streamid in (select id from datastream_t where deviceid = '246088802');

-- delete from datapoints_t where datapoints_t.streamid = datastream_t.id and datastream_t.deviceid = '43720';

delete from datapoints_t;
delete from datastream_t;
delete from device_t;
