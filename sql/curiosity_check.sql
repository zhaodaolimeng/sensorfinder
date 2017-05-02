SELECT s.deviceid, s.alias, p.streamid, p.at, p.value 
FROM datapoints_t as p, datastream_t as s
where p.streamid = s.id and p.at>'2015-06-29' order by s.deviceid, s.alias, p.at;