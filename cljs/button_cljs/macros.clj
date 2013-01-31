;;----------------------------------------------------------------------
;; File ces.clj
;; Written by Chris
;; 
;; Created 27 Jan 2013
;; Last modified 30 Jan 2013
;; 
;; Clojure code (using macros) for defining a component entity system
;;----------------------------------------------------------------------

(ns button-cljs.macros)

#_(def ^{:private true} all-entities (atom {:next-id 0}))

#_(defn ^{:private true}
  $all-entities-with
  "Returns all entities with a given component."
  [all-entities cmp-key]
  `(for [[id entity] (deref ~all-entities)
        :when (get entity ~cmp-key)]
    entity))

(defmacro all-entities-with
  [all-entities cmp]
  `(let [cmp-key# ~(keyword cmp)]
     (for [[id# entity#] (deref ~all-entities)
         :when (get entity# cmp-key#)]
     entity)))

#_(defn $defentity
  [all-entities cmp-key* cmp*]
  `(let [next-id (:next-id (deref ~all-entities))
         emap (apply assoc {} (interleave ~cmp-key* ~cmp*))]
     (swap! ~all-entities assoc next-id emap)
     (swap! ~all-entities assoc :next-id (inc next-id))))

(defmacro defentity
  [all-entities cmp*]
  `(let [next-id# (:next-id (deref ~all-entities))
         emap# (apply assoc
                {}
                (interleave (for [cmp# ~cmp*] (keyword (first cmp#))) ~cmp*))]
     (swap! ~all-entities
            (fn [all-e#]
              (let [entities# (:entities all-e#)]
                (assoc all-e :entities (assoc entities# next-id# emap#)))))
     (swap! ~all-entities assoc :next-id (inc next-id#))))

(defmacro get-entity-by-id
  [all-entities id]
  `(get (deref ~all-entities) ~id))  

(defmacro defcomponent
  [name args & field+val*]
  `(defn ~name ~args (hash-map ~@field+val*)))

#_(defn ^{:private true}
  $entity-add-comp
  [all-entities eid cmp-keyword cmp-fn & args]
  `(swap! ~all-entities
     (fn [all-e]
       (assoc all-e ~eid
         (assoc (get all-e ~eid) ~cmp-keyword
           (apply ~cmp-fn ~args))))))

(defmacro entity-add-comp
  [all-entities eid cmp & args]
  `(swap! ~all-entities
     (fn [all-e#]
       (assoc all-e# ~eid
         (assoc (get all-e# ~eid) ~(keyword cmp)
           (apply ~cmp ~args))))))

(defmacro entity-get-comp
  [ent cmp]
  `(get ~ent ~(keyword cmp)))

#_(defmacro defsystem
  [all-entities sys-name cmp cmp-fn]
  `(defn ~sys-name []
     (let [entity*# (all-entities-with ~(keyword cmp))]
       (doseq [entity# entity*#]
         (~cmp-fn entity#)))))
