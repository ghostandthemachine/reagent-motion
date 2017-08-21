(ns motion.util)


(defn- now
  []
  (str "t" (.now js/Date)))


(defn index-of
  [coll value]
  (some (fn [[idx item]]
          (when (= value item)
            idx))
        (map-indexed vector coll)))


(defn clamp
  [n a b]
  (max (min n b) a))


(defn re-insert
  [xs from to]
  (let [xs (into [] xs)
        [from to]   [(Math/min from to) (Math/max from to)]
        x           (nth xs from)]
    (vec (concat (subvec xs 0 from)
                 (subvec xs (inc from) (inc to))
                 [x]
                 (subvec xs (inc to))))))
