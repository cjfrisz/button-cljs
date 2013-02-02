;;----------------------------------------------------------------------
;; File ces.cljs
;; Written by Chris
;; 
;; Created 30 Jan 2013
;; Last modified 30 Jan 2013
;; 
;; Wrapper for the Clojure-based CES macros
;;----------------------------------------------------------------------

(ns button-cljs.ces
  (:require-macros [button-cljs.macros :as macros]))

(def all-entities (atom []))
