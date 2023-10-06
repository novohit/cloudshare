-- 判断锁是否为该线程所持有
if redis.call('GET', KEYS[1]) == ARGV[1]
then
    return redis.call('DEL', KEYS[1])
else
    return 0
end