;;----------------------------------------------------------------------
;; File game_state.cljs
;; Written by Chris
;;
;; Created 23 Jan 2013
;; Last modified 27 Jan 2013
;;
;; Provides the API for querying and modifying game state
;;----------------------------------------------------------------------

(ns button-cljs.game-state
  (:use [button-cljs.util :only (cur-time)]))

(declare new-flip-time)

(defn make-game-state
  "Create a new, unspoiled game state."
  []
  {:button-on          false,
   :hit                0,
   :miss               0,
   :last-update        (cur-time),
   :flip-time          (new-flip-time)}) 

;;-------------------------
;; Game state accessors
;;-------------------------

(defn get-button-on [game-state] (:button-on game-state))

(defn get-hit [game-state] (:hit game-state))

(defn get-miss [game-state] (:miss game-state))

(defn get-last-update [game-state] (:last-update game-state))

(defn get-flip-time [game-state] (:flip-time game-state))

;;-------------------------
;; Game state updaters
;;-------------------------

;; Note that these are mutators since they don't use mutation

(defn set-button-on
  [game-state button-on]
  (assoc game-state :button-on button-on))

(defn set-hit
  [game-state hit]
  (assoc game-state :hit hit))

(defn set-miss
  [game-state miss]
  (assoc game-state :miss miss))

(defn set-last-update
  [game-state last-update]
  (assoc game-state :last-update last-update))

(defn set-flip-time
  [game-state flip-time]
  (assoc game-state :flip-time flip-time))

;;-------------------------
;; Convenience functions
;;-------------------------

(defn flip-button
  [game-state]
  (set-button-on game-state (not (get-button-on game-state))))

(defn inc-score
  [game-state]
  (let [[setter getter] (if (get-button-on game-state)
                            [set-hit get-hit]
                            [set-miss get-miss])]
    (setter game-state (inc (getter game-state)))))

(def button-flip-min-time 1000)
(def button-flip-max-time 5000)

(defn new-flip-time
  "Returns a number of milliseconds for the next button flip in the range
  specified by button-flip-min-time and button-flip-max-time."
  []
  (let [flip-time (rand-int button-flip-max-time)]
    (if (>= flip-time button-flip-min-time)
        flip-time
        (recur))))
