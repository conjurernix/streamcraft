(ns streamcraft.redis-store-carmine.core
  (:refer-clojure :exclude [get])
  (:require [com.stuartsierra.component :as component]
            [streamcraft.protocols.api.redis-store :as redis-store]
            [taoensso.carmine :as car]))

(defrecord CarmineRedisStore [config pool wcar-opts commands]
  component/Lifecycle
  (start [this]
    (let [{:keys [pool-opts spec]} config
          pool (car/connection-pool pool-opts)
          wcar-opts {:pool pool :spec spec}
          initial-commands []]
      (-> this
          (assoc :pool pool)
          (assoc :wcar-opts wcar-opts)
          (assoc :commands initial-commands))))

  (stop [this]
    (.close pool)
    (-> this
        (assoc :config nil)
        (assoc :pool nil)
        (assoc :wcar-opts nil)
        (assoc :commands nil)))

  redis-store/IRedisStore
  (run! [_]
    (car/wcar wcar-opts
      (for [[command & args] commands]
        (apply command args))))

  (run! [_ {:keys [pipeline]}]
    (car/wcar wcar-opts
      (when pipeline
        :as-pipeline)
      (doseq [[command & args] commands]
        (apply command args))))

  (clear-commands [this]
    (-> this
        (update :commands empty)))

  (-get [this key]
    (-> this
        (update :commands conj [car/get key])))

  (-set [this key val]
    (-> this
        (update :commands conj [car/set key val])))

  (-del [this key]
    (-> this
        (update :commands conj [car/del key])))

  (-exists [this key]
    (-> this
        (update :commands conj [car/exists key])))

  (-keys [this pattern]
    (-> this
        (update :commands conj [car/keys pattern])))

  (-expire [this key seconds]
    (-> this
        (update :commands conj [car/expire key seconds])))

  (-persist [this key]
    (-> this
        (update :commands conj [car/persist key])))

  (-incr [this key]
    (-> this
        (update :commands conj [car/incr key])))

  (-decr [this key]
    (-> this
        (update :commands conj [car/decr key])))

  (-lpush [this key value]
    (-> this
        (update :commands conj [car/lpush key value])))

  (-rpush [this key value]
    (-> this
        (update :commands conj [car/rpush key value])))

  (-lpop [this key]
    (-> this
        (update :commands conj [car/lpop key])))

  (-rpop [this key]
    (-> this
        (update :commands conj [car/rpop key])))

  (-llen [this key]
    (-> this
        (update :commands conj [car/llen key])))

  (-lrange [this key start stop]
    (-> this
        (update :commands conj [car/lrange key start stop])))

  (-lindex [this key index]
    (-> this
        (update :commands conj [car/lindex key index])))

  (-lset [this key index value]
    (-> this
        (update :commands conj [car/lset key index value])))

  (-linsert [this key pivot value direction]
    (-> this
        (update :commands conj [car/linsert key pivot value direction])))

  (-lrem [this key count value]
    (-> this
        (update :commands conj [car/lrem key count value])))

  (-sadd [this key member]
    (-> this
        (update :commands conj [car/sadd key member])))

  (-srem [this key member]
    (-> this
        (update :commands conj [car/srem key member])))

  (-scard [this key]
    (-> this
        (update :commands conj [car/scard key])))

  (-smembers [this key]
    (-> this
        (update :commands conj [car/smembers key])))

  (-sismember [this key member]
    (-> this
        (update :commands conj [car/sismember key member])))

  (-sunion [this keys]
    (-> this
        (update :commands conj [car/sunion keys])))

  (-sinter [this keys]
    (-> this
        (update :commands conj [car/sinter keys])))

  (-sdiff [this keys]
    (-> this
        (update :commands conj [car/sdiff keys])))

  (-srandmember [this key]
    (-> this
        (update :commands conj [car/srandmember key])))

  (-srandmember [this key count]
    (-> this
        (update :commands conj [car/srandmember key count])))

  (-spop [this key]
    (-> this
        (update :commands conj [car/spop key])))

  (-spop [this key count]
    (-> this
        (update :commands conj [car/spop key count])))

  (-hset [this key field value]
    (-> this
        (update :commands conj [car/hset key field value])))

  (-hget [this key field]
    (-> this
        (update :commands conj [car/hget key field])))

  (-hdel [this key field]
    (-> this
        (update :commands conj [car/hdel key field])))

  (-hlen [this key]
    (-> this
        (update :commands conj [car/hlen key])))

  (-hkeys [this key]
    (-> this
        (update :commands conj [car/hkeys key])))

  (-hvals [this key]
    (-> this
        (update :commands conj [car/hvals key])))

  (-zadd [this key score member]
    (-> this
        (update :commands conj [car/zadd key score member])))

  (-zrange [this key start stop]
    (-> this
        (update :commands conj [car/zrange key start stop])))

  (-zrem [this key member]
    (-> this
        (update :commands conj [car/zrem key member])))

  (-zcard [this key]
    (-> this
        (update :commands conj [car/zcard key])))

  (-zscore [this key member]
    (-> this
        (update :commands conj [car/zscore key member]))))

(defn make-redis-store []
  (map->CarmineRedisStore {}))
