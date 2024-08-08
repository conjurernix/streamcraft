(ns streamcraft.protocols.api.redis-store
  (:refer-clojure :exclude [dec get inc keys pop set]))

(defprotocol IRedisStore
  "Protocol for interacting with Redis or Redis-compatible key-value store."

  (run! [this] [this opts]
    "Run the commands in the store.")

  (clear-commands [this]
    "Clear the commands in the store.")

  (get [this key]
    "Retrieve the value associated with the given key.")

  (set [this key val]
    "Set the value associated with the key.")

  (del [this key]
    "Delete the key and its associated value.")

  (exists [this key]
    "Check if a key exists in the store.")

  (keys [this pattern]
    "Retrieve the keys matching a certain pattern.")

  (expire [this key seconds]
    "Set a key to expire after a specified number of seconds.")

  (persist [this key]
    "Remove the existing timeout on key, making the key persistent (i.e., it will never expire).")

  (inc [this key]
    "Increment the integer value of a key by one.")

  (dec [this key]
    "Decrement the integer value of a key by one.")

  (push [this key val]
    "Add the `val` to the list stored at `key`.")

  (pop [this key]
    "Remove and return an element from the list stored at `key`.")

  (lpush [this key value]
    "Insert `value` at the front of the list stored at `key`.")

  (rpush [this key value]
    "Insert `value` at the end of the list stored at `key`.")

  (lpop [this key]
    "Remove and return the first element of the list stored at `key`.")

  (rpop [this key]
    "Remove and return the last element of the list stored at `key`.")

  (llen [this key]
    "Return the length of the list stored at `key`.")

  (lrange [this key start stop]
    "Get a range of elements from the list stored at `key` from `start` to `stop`.")

  (lindex [this key index]
    "Get an element from a list by its index.")

  (lset [this key index value]
    "Set the value of an element in a list by its index.")

  (linsert [this key pivot value direction]
    "Insert `value` in the list stored at `key` either before or after the first occurrence of `pivot` depending on the `direction`.")

  (lrem [this key count value]
    "Remove elements equal to `value` from the list stored at `key`.")

  (sadd [this key member]
    "Add `member` to the set stored at `key`.")

  (srem [this key member]
    "Remove `member` from the set stored at `key`.")

  (scard [this key]
    "Get the number of members in a set.")

  (smembers [this key]
    "Return a list of all the members in the set at `key`.")

  (sismember [this key member]
    "Check if `member` is a member of the set at `key`.")

  (sunion [this keys]
    "Return the members of the set resulting from the union of all the given sets.")

  (sinter [this keys]
    "Return the members of the set resulting from the intersection of all the given sets.")

  (sdiff [this keys]
    "Return the members of the set resulting from the difference of the first set from all the successive sets.")

  (srandmember [this key] [this key count]
    "Get one or multiple random members from a set.")

  (spop [this key] [this key count]
    "Remove and return one or multiple random members from a set.")

  (hset [this key field value]
    "Set the `value` of the `field` in the hash stored at `key`.")

  (hget [this key field]
    "Get the value of the `field` in the hash stored at `key`.")

  (hdel [this key field]
    "Delete the `field` from the hash stored at `key`.")

  (hlen [this key]
    "Get the number of fields in a hash.")

  (hkeys [this key]
    "`Return` all field names in the hash stored at `key`.")

  (hvalues [this key]
    "`Return` all values in the hash stored at `key`.")

  (zpadd [this key score member]
    "Add `member` with `score` to the sorted set stored at `key`.")

  (zrange [this key start stop]
    "Return a range of members in a sorted set, by index.")

  (zrem [this key member]
    "Remove `member` from the sorted set stored at `key`.")

  (zcard [this key]
    "Get the number of members in a sorted set.")

  (zscore [this key member]
    "Get the score associated with the given `member` in a sorted set."))
