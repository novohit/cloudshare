-- 索引从1开始
local key = KEYS[1]
local time = tonumber(ARGV[1])
local count = tonumber(ARGV[2])
-- 当前要限流的那个接口存在redis里的key 之前没调用过, key可能为空
local current = redis.call('get', key)
-- 如果current调用次数不空且超过限流次数 则返回
if current and tonumber(current) > count
then
    -- 这里也可以继续自增 到达一定次数 封禁IP
    return tonumber(current)
end
-- 如果是第一次访问该接口 则设置一个key次数增加1
-- 为什么不是直接设置为1而是选择自增 因为考虑并发状况 如果另一个也执行了set 1 那么就无法判断是否只有一次操作
current = redis.call('incr', key)
-- 并发情况 另一个线程也执行了自增1 再次判断 如果还是1就设置一个过期时间
if tonumber(current) == 1
then
    redis.call('expire', key, time)
end
return tonumber(current)