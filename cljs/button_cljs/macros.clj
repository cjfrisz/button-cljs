;;----------------------------------------------------------------------
;; File ces.clj
;; Written by Chris
;; 
;; Created 27 Jan 2013
;; Last modified 27 Jan 2013
;; 
;; Clojure code (using macros) for defining a component entity system
;;----------------------------------------------------------------------

(ns button-cljs.macros)

(def ^{:private true} all-entities (atom {:next-id 0}))

(defn ^{:private true}
  $all-entities-with
  "Returns all entities with a given component."
  [cmp-key]
  (for [[id entity] @all-entities
        :when (get entity cmp-key)]
    entity))

(defmacro all-entities-with
  [cmp]
  `($all-entities-with ~(keyword cmp)))

(defn $defentity
  [cmp-key* cmp*]
  (let [next-id (:next-id @all-entities)
        emap (apply assoc {} (interleave cmp-key* cmp*))]
    (swap! all-entities assoc next-id emap)
    (swap! all-entities assoc :next-id (inc next-id))
    next-id))

(defmacro defentity
  [cmp*]
  `($defentity (for [cmp# ~cmp*] (first cmp#)) ~cmp*))

(defn get-entity-by-id [id] (get @all-entities id))  

(defmacro defcomponent
  [name args & field+val*]
  (when-not (zero? (mod (count field+val*) 2))
    (throw
     (Exeption. "component expects an even number of fields/value pairs.")))
  `(defn ~name ~args {~@field+val*}))

(defn ^{:private true}
  $entity-add-comp
  [eid cmp-keyword cmp-fn & args]
  (swap! all-entities (fn [all-e]
                        (assoc all-e eid
                               (assoc (get all-e eid) cmp-keyword
                                      (apply cmp-fn args))))))

(defmacro entity-add-comp
  [eid cmp & args]
  `($entity-add-comp eid ~(keyword cmp) ~cmp ~@args))

(defmacro entity-get-comp
  [ent cmp]
  `(get ~ent ~(keyword cmp)))

(defmacro defsystem
  [sys-name cmp cmp-fn]
  `(defn ~sys-name []
     (let [entity*# (all-entities-with ~(keyword cmp))]
       (doseq [entity# entity*#]
         (~cmp-fn entity#)))))
