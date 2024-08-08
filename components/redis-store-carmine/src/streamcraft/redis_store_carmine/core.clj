(ns streamcraft.redis-store-carmine.core
  (:require [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.redis-store :as redis-store]
            [taoensso.carmine :as car]))

(defrecord CarmineRedisStore [config pool wcar-opts]
  component/Lifecycle
  (start [this]
    (let [{:keys [pool-opts spec]} config
          pool (car/connection-pool pool-opts)
          wcar-opts {:pool pool :spec spec}]
      (-> this
          (assoc :pool pool)
          (assoc :wcar-opts wcar-opts))))

  (stop [this]
    (.close pool)
    (-> this
        (assoc :config nil)
        (assoc :pool nil)
        (assoc :wcar-opts nil)))

  redis-store/IRedisStore
  (get [_ key]
    (car/wcar wcar-opts
      (car/get key)))

  (set [_ key val]
    (car/wcar wcar-opts
      (car/set key val)))

  (del [_ key]
    (car/wcar wcar-opts
      (car/del key)))

  (exists [_ key]
    (car/wcar wcar-opts
      (car/exists key)))

  (keys [_ pattern]
    (car/wcar wcar-opts
      (car/keys pattern)))

  (expire [_ key seconds]
    (car/wcar wcar-opts
      (car/expire key seconds)))

  (persist [_ key]
    (car/wcar wcar-opts
      (car/persist key)))

  (inc [_ key]
    (car/wcar wcar-opts
      (car/inc key)))

  (dec [_ key]
    (car/wcar wcar-opts
      (car/dec key)))

  (push [_ key val]
    (car/wcar wcar-opts
      (car/push key val)))

  (pop [_ key]
    (car/wcar wcar-opts
      (car/pop key)))

  (lpush [_ key value]
    (car/wcar wcar-opts
      (car/lpush key value)))

  (rpush [_ key value]
    (car/wcar wcar-opts
      (car/rpush key value)))

  (lpop [_ key]
    (car/wcar wcar-opts
      (car/lpop key)))

  (rpop [_ key]
    (car/wcar wcar-opts
      (car/rpop key)))

  (llen [_ key]
    (car/wcar wcar-opts
      (car/llen key)))

  (lrange [_ key start stop]
    (car/wcar wcar-opts
      (car/lrange key start stop)))

  (lindex [_ key index]
    (car/wcar wcar-opts
      (car/lindex key index)))

  (lset [_ key index value]
    (car/wcar wcar-opts
      (car/lset key index value)))

  (linsert [_ key pivot value direction]
    (car/wcar wcar-opts
      (car/linsert key pivot value direction)))

  (lrem [_ key count value]
    (car/wcar wcar-opts
      (car/lrem key count value)))

  (sadd [_ key member]
    (car/wcar wcar-opts
      (car/sadd key member)))

  (srem [_ key member]
    (car/wcar wcar-opts
      (car/srem key member)))

  (scard [_ key]
    (car/wcar wcar-opts
      (car/scard key)))

  (smembers [_ key]
    (car/wcar wcar-opts
      (car/smembers key)))

  (sismember [_ key member]
    (car/wcar wcar-opts
      (car/sismember key member)))

  (sunion [_ keys]
    (car/wcar wcar-opts
      (car/sunion keys)))

  (sinter [_ keys]
    (car/wcar wcar-opts
      (car/sinter keys)))

  (sdiff [_ keys]
    (car/wcar wcar-opts
      (car/sdiff keys)))

  (srandmember [_ key]
    (car/wcar wcar-opts
      (car/srandmember key)))

  (srandmember [_ key count]
    (car/wcar wcar-opts
      (car/srandmember key count)))

  (spop [_ key]
    (car/wcar wcar-opts
      (car/spop key)))

  (spop [_ key count]
    (car/wcar wcar-opts
      (car/spop key count)))

  (hset [_ key field value]
    (car/wcar wcar-opts
      (car/hset key field value)))

  (hget [_ key field]
    (car/wcar wcar-opts
      (car/hget key field)))

  (hdel [_ key field]
    (car/wcar wcar-opts
      (car/hdel key field)))

  (hlen [_ key]
    (car/wcar wcar-opts
      (car/hlen key)))

  (hkeys [_ key]
    (car/wcar wcar-opts
      (car/hkeys key)))

  (hvalues [_ key]
    (car/wcar wcar-opts
      (car/hvalues key)))

  (zpadd [_ key score member]
    (car/wcar wcar-opts
      (car/zpadd key score member)))

  (zrange [_ key start stop]
    (car/wcar wcar-opts
      (car/zrange key start stop)))

  (zrem [_ key member]
    (car/wcar wcar-opts
      (car/zrem key member)))

  (zcard [_ key]
    (car/wcar wcar-opts
      (car/zcard key)))

  (zscore [_ key member]
    (car/wcar wcar-opts
      (car/zscore key member))))
