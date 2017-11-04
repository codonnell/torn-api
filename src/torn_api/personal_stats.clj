(ns torn-api.personal-stats
  (:require [clojure.spec.alpha :as s]))

(def nonneg-int? (s/and integer? #(>= % 0)))

(s/def ::logins nonneg-int?)
(s/def ::activity nonneg-int?)
(s/def ::attacks-lost nonneg-int?)
(s/def ::attacks-won nonneg-int?)
(s/def ::attacks-draw nonneg-int?)
(s/def ::highest-beaten nonneg-int?)
(s/def ::best-kill-streak nonneg-int?)
(s/def ::defends-lost nonneg-int?)
(s/def ::defends-won nonneg-int?)
(s/def ::defends-draw nonneg-int?)
(s/def ::xanax-taken nonneg-int?)
(s/def ::ecstasy-taken nonneg-int?)
(s/def ::times-traveled nonneg-int?)
(s/def ::networth  nonneg-int?)
(s/def ::refills  nonneg-int?)
(s/def ::stat-enhancers-used  nonneg-int?)
(s/def ::medical-items-used  nonneg-int?)
(s/def ::weapons-bought  nonneg-int?)
(s/def ::bazaar-customers  nonneg-int?)
(s/def ::bazaar-sales  nonneg-int?)
(s/def ::bazaar-profit  nonneg-int?)
(s/def ::points-bought  nonneg-int?)
(s/def ::points-sold  nonneg-int?)
(s/def ::items-bought-abroad  nonneg-int?)
(s/def ::items-bought  nonneg-int?)
(s/def ::trades  nonneg-int?)
(s/def ::items-sent  nonneg-int?)
(s/def ::auctions-won  nonneg-int?)
(s/def ::auctions-sold  nonneg-int?)
(s/def ::money-mugged  nonneg-int?)
(s/def ::attacks-stealthed  nonneg-int?)
(s/def ::critical-hits  nonneg-int?)
(s/def ::total-respect  nonneg-int?)
(s/def ::rounds-fired  nonneg-int?)
(s/def ::attacks-ran-away  nonneg-int?)
(s/def ::defends-ran-away  nonneg-int?)
(s/def ::people-busted  nonneg-int?)
(s/def ::failed-busts  nonneg-int?)
(s/def ::bails-bought  nonneg-int?)
(s/def ::bails-spent  nonneg-int?)
(s/def ::viruses-coded  nonneg-int?)
(s/def ::city-finds  nonneg-int?)
(s/def ::bounties-placed  nonneg-int?)
(s/def ::bounties-received  nonneg-int?)
(s/def ::bounties-collected  nonneg-int?)
(s/def ::total-bounty-rewards  nonneg-int?)
(s/def ::total-bounty-spent  nonneg-int?)
(s/def ::revives  nonneg-int?)
(s/def ::revives-received  nonneg-int?)
(s/def ::trains-received  nonneg-int?)
(s/def ::drugs-taken  nonneg-int?)
(s/def ::overdoses  nonneg-int?)
(s/def ::merits-bought  nonneg-int?)
(s/def ::personals-placed  nonneg-int?)
(s/def ::classifieds-placed  nonneg-int?)
(s/def ::mail-sent  nonneg-int?)
(s/def ::faction-mail-sent  nonneg-int?)
(s/def ::friend-mail-sent  nonneg-int?)
(s/def ::company-mail-sent  nonneg-int?)
(s/def ::spouse-mail-sent  nonneg-int?)
(s/def ::largest-mug  nonneg-int?)
(s/def ::canabis-taken  nonneg-int?)
(s/def ::ketamine-taken  nonneg-int?)
(s/def ::lsd-taken  nonneg-int?)
(s/def ::opium-taken  nonneg-int?)
(s/def ::shrooms-taken  nonneg-int?)
(s/def ::speed-taken  nonneg-int?)
(s/def ::pcp-taken  nonneg-int?)
(s/def ::vicodin-taken  nonneg-int?)
(s/def ::mechanical-hits  nonneg-int?)
(s/def ::artillery-hits  nonneg-int?)
(s/def ::clubbed-hits  nonneg-int?)
(s/def ::temp-hits  nonneg-int?)
(s/def ::machine-gun-hits  nonneg-int?)
(s/def ::pistol-hits  nonneg-int?)
(s/def ::rifle-hits  nonneg-int?)
(s/def ::shotgun-hits  nonneg-int?)
(s/def ::smg-hits  nonneg-int?)
(s/def ::piercing-hits  nonneg-int?)
(s/def ::slashing-hits  nonneg-int?)
(s/def ::argentina-travel  nonneg-int?)
(s/def ::mexico-travel  nonneg-int?)
(s/def ::dubai-travel  nonneg-int?)
(s/def ::hawaii-travel  nonneg-int?)
(s/def ::japan-travel  nonneg-int?)
(s/def ::london-travel  nonneg-int?)
(s/def ::sa-travel  nonneg-int?)
(s/def ::switzerland-travel  nonneg-int?)
(s/def ::china-travel  nonneg-int?)
(s/def ::canada-travel  nonneg-int?)
(s/def ::dump-finds  nonneg-int?)
(s/def ::dump-searches  nonneg-int?)
(s/def ::items-dumped  nonneg-int?)
(s/def ::days-as-donator  nonneg-int?)
(s/def ::caymans-travel  nonneg-int?)
(s/def ::times-jailed  nonneg-int?)
(s/def ::times-hospitalized  nonneg-int?)
(s/def ::attacks-assisted  nonneg-int?)
(s/def ::blood-withdrawn  nonneg-int?)
(s/def ::mission-credits  nonneg-int?)
(s/def ::contracts-completed  nonneg-int?)
(s/def ::duke-contracts-completed  nonneg-int?)
(s/def ::missions-completed  nonneg-int?)
(s/def ::meds-stolen nonneg-int?)
(s/def ::spies-done nonneg-int?)
(s/def ::personal-stats
  (s/keys :opt [::auctions-won ::points-sold ::piercing-hits ::mexico-travel
                ::xanax-taken ::trades ::sa-travel ::drugs-taken ::money-mugged
                ::bails-bought ::clubbed-hits ::canada-travel ::overdoses
                ::times-traveled ::city-finds ::defends-draw ::mission-credits
                ::items-bought ::slashing-hits ::company-mail-sent ::smg-hits
                ::networth ::ketamine-taken ::attacks-assisted ::bounties-collected
                ::friend-mail-sent ::highest-beaten ::logins ::opium-taken
                ::bazaar-sales ::times-jailed ::best-kill-streak ::merits-bought
                ::argentina-travel ::spouse-mail-sent ::vicodin-taken ::faction-mail-sent
                ::trains-received ::largest-mug ::rounds-fired ::bounties-received
                ::duke-contracts-completed ::pistol-hits ::defends-lost ::activity
                ::hawaii-travel ::bails-spent ::viruses-coded ::speed-taken
                ::weapons-bought ::rifle-hits ::shrooms-taken ::attacks-stealthed
                ::personals-placed ::china-travel ::london-travel ::dubai-travel
                ::auctions-sold ::revives-received ::people-busted ::bazaar-customers
                ::classifieds-placed ::switzerland-travel ::dump-searches
                ::machine-gun-hits ::failed-busts ::caymans-travel ::temp-hits
                ::stat-enhancers-used ::canabis-taken ::times-hospitalized
                ::days-as-donator ::total-bounty-spent ::total-respect ::lsd-taken
                ::items-dumped ::medical-items-used ::attacks-ran-away ::attacks-lost
                ::critical-hits ::japan-travel ::shotgun-hits ::artillery-hits
                ::bazaar-profit ::missions-completed ::refills ::bounties-placed
                ::attacks-won ::ecstasy-taken ::contracts-completed ::points-bought
                ::defends-ran-away ::mail-sent ::dump-finds ::items-bought-abroad
                ::attacks-draw ::pcp-taken ::blood-withdrawn ::items-sent
                ::total-bounty-rewards ::revives ::mechanical-hits ::defends-won
                ::meds-stolen ::spies-done]))

(def key-coercions
  {
   ::logins {:path [:personalstats :logins]}
   ::activity {:path [:personalstats :useractivity]}
   ::attacks-lost {:path [:personalstats :attackslost]}
   ::attacks-won {:path [:personalstats :attackswon]}
   ::attacks-draw {:path [:personalstats :attacksdraw]}
   ::highest-beaten {:path [:personalstats :highestbeaten]}
   ::best-kill-streak {:path [:personalstats :bestkillstreak]}
   ::defends-lost {:path [:personalstats :defendslost]}
   ::defends-won {:path [:personalstats :defendswon]}
   ::defends-draw {:path [:personalstats :defendsstalemated]}
   ::xanax-taken {:path [:personalstats :xantaken]}
   ::ecstasy-taken {:path [:personalstats :exttaken]}
   ::times-traveled {:path [:personalstats :traveltimes]}
   ::networth  {:path [:personalstats :networth]}
   ::refills  {:path [:personalstats :refills] :coercer #(or % 0)}
   ::stat-enhancers-used  {:path [:personalstats :statenhancersused]}
   ::medical-items-used  {:path [:personalstats :medicalitemsused]}
   ::weapons-bought  {:path [:personalstats :weaponsbought]}
   ::bazaar-customers  {:path [:personalstats :bazaarcustomers]}
   ::bazaar-sales  {:path [:personalstats :bazaarsales]}
   ::bazaar-profit  {:path [:personalstats :bazaarprofit]}
   ::points-bought  {:path [:personalstats :pointsbought]}
   ::points-sold  {:path [:personalstats :pointssold]}
   ::items-bought-abroad  {:path [:personalstats :itemsboughtabroad]}
   ::items-bought  {:path [:personalstats :itemsbought]}
   ::trades  {:path [:personalstats :trades]}
   ::items-sent  {:path [:personalstats :itemssent]}
   ::auctions-won  {:path [:personalstats :auctionswon]}
   ::auctions-sold  {:path [:personalstats :auctionsells]}
   ::money-mugged  {:path [:personalstats :moneymugged]}
   ::attacks-stealthed  {:path [:personalstats :attacksstealthed]}
   ::critical-hits  {:path [:personalstats :attackcriticalhits]}
   ::total-respect  {:path [:personalstats :respectforfaction]}
   ::rounds-fired  {:path [:personalstats :roundsfired]}
   ::attacks-ran-away  {:path [:personalstats :yourunaway]}
   ::defends-ran-away  {:path [:personalstats :theyrunaway]}
   ::people-busted  {:path [:personalstats :peoplebusted]}
   ::failed-busts  {:path [:personalstats :failedbusts]}
   ::bails-bought  {:path [:personalstats :peoplebought]}
   ::bails-spent  {:path [:personalstats :peopleboughtspent]}
   ::viruses-coded  {:path [:personalstats :virusescoded]}
   ::city-finds  {:path [:personalstats :cityfinds]}
   ::bounties-placed  {:path [:personalstats :bountiesplaced]}
   ::bounties-received  {:path [:personalstats :bountiesreceived]}
   ::bounties-collected  {:path [:personalstats :bountiescollected]}
   ::total-bounty-rewards  {:path [:personalstats :totalbountyreward]}
   ::total-bounty-spent  {:path [:personalstats :totalbountyspent]}
   ::revives  {:path [:personalstats :revives]}
   ::revives-received  {:path [:personalstats :revivesreceived]}
   ::trains-received  {:path [:personalstats :trainsreceived]}
   ::drugs-taken  {:path [:personalstats :drugsused]}
   ::overdoses  {:path [:personalstats :overdosed]}
   ::merits-bought  {:path [:personalstats :meritsbought]}
   ::personals-placed  {:path [:personalstats :personalsplaced]}
   ::classifieds-placed  {:path [:personalstats :classifiedadsplaced]}
   ::mail-sent  {:path [:personalstats :mailssent]}
   ::faction-mail-sent  {:path [:personalstats :friendmailssent]}
   ::friend-mail-sent  {:path [:personalstats :factionmailssent]}
   ::company-mail-sent  {:path [:personalstats :companymailssent]}
   ::spouse-mail-sent  {:path [:personalstats :spousemailssent]}
   ::largest-mug  {:path [:personalstats :largestmug]}
   ::canabis-taken  {:path [:personalstats :cantaken]}
   ::ketamine-taken  {:path [:personalstats :kettaken]}
   ::lsd-taken  {:path [:personalstats :lsdtaken]}
   ::opium-taken  {:path [:personalstats :opitaken]}
   ::shrooms-taken  {:path [:personalstats :shrtaken]}
   ::speed-taken  {:path [:personalstats :spetaken]}
   ::pcp-taken  {:path [:personalstats :pcptaken]}
   ::vicodin-taken  {:path [:personalstats :victaken]}
   ::mechanical-hits  {:path [:personalstats :chahits]}
   ::artillery-hits  {:path [:personalstats :heahits]}
   ::clubbed-hits  {:path [:personalstats :axehits]}
   ::temp-hits  {:path [:personalstats :grehits]}
   ::machine-gun-hits  {:path [:personalstats :machits]}
   ::pistol-hits  {:path [:personalstats :pishits]}
   ::rifle-hits  {:path [:personalstats :rifhits]}
   ::shotgun-hits  {:path [:personalstats :shohits]}
   ::smg-hits  {:path [:personalstats :smghits]}
   ::piercing-hits  {:path [:personalstats :piehits]}
   ::slashing-hits  {:path [:personalstats :slahits]}
   ::argentina-travel  {:path [:personalstats :argtravel]}
   ::mexico-travel  {:path [:personalstats :mextravel]}
   ::dubai-travel  {:path [:personalstats :dubtravel]}
   ::hawaii-travel  {:path [:personalstats :hawtravel]}
   ::japan-travel  {:path [:personalstats :japtravel]}
   ::london-travel  {:path [:personalstats :lontravel]}
   ::sa-travel  {:path [:personalstats :soutravel]}
   ::switzerland-travel  {:path [:personalstats :switravel]}
   ::china-travel  {:path [:personalstats :chitravel]}
   ::canada-travel  {:path [:personalstats :cantravel]}
   ::dump-finds  {:path [:personalstats :dumpfinds]}
   ::dump-searches  {:path [:personalstats :dumpsearches]}
   ::items-dumped  {:path [:personalstats :itemsdumped]}
   ::days-as-donator  {:path [:personalstats :daysbeendonator]}
   ::caymans-travel  {:path [:personalstats :caytravel]}
   ::times-jailed  {:path [:personalstats :jailed]}
   ::times-hospitalized  {:path [:personalstats :hospital]}
   ::attacks-assisted  {:path [:personalstats :attacksassisted]}
   ::blood-withdrawn  {:path [:personalstats :bloodwithdrawn]}
   ::mission-credits  {:path [:personalstats :missioncreditsearned]}
   ::contracts-completed  {:path [:personalstats :contractscompleted]}
   ::duke-contracts-completed  {:path [:personalstats :dukecontractscompleted]}
   ::missions-completed  {:path [:personalstats :missionscompleted]}
   ::meds-stolen {:path [:personalstats :medstolen]}
   ::spies-done {:path [:personalstats :spydone]}
   })

(defn coerce
  "Coerces a profile API response so that it satisfies the :torn-api.profile/profile spec."
  [body]
  (reduce-kv (fn [m k {:keys [path coercer] :or {coercer identity}}]
               (let [v (-> body (get-in path) coercer)]
                 (if (nil? v) m (assoc m k v))))
             {}
             key-coercions))
