;;----------------------------------------------------------------------
;; File comps.cljs
;; Written by Chris
;; 
;; Created 27 Jan 2013
;; Last modified 27 Jan 2013
;; 
;; Defines the components for the button game.
;;----------------------------------------------------------------------

(ns button-cljs.comps
  (:require-macros [button-cljs.macros :refer [defcomponent]]))

(defcomponent init [priority init-fn]
  :priority priority
  :init-fn init-fn)

(defcomponent page-element [tag-id]
  :tag-id tag-id)
