---
title: PUT :domain/config/endpoints/:id | REST API Documentation
layout: default
---

<div id="menu" markdown="1">
Contents
--------

### ChangesResource

* [POST :domain/changes/:endpoint](/doc/rest/p_domain/changes/post/p_endpoint)

### ConfigurationResource

* [GET :domain/config/pairs/:id](/doc/rest/p_domain/config/get/pairs/p_id)
* [POST :domain/config/xml](/doc/rest/p_domain/config/post/xml)
* [GET :domain/config/members](/doc/rest/p_domain/config/get/members)
* [GET :domain/config/repair-actions](/doc/rest/p_domain/config/get/repair-actions)
* [GET :domain/config/endpoints](/doc/rest/p_domain/config/get/endpoints)
* [GET :domain/config/xml](/doc/rest/p_domain/config/get/xml)
* [DELETE :domain/config/endpoints/:id](/doc/rest/p_domain/config/delete/endpoints/p_id)
* [DELETE :domain/config/pairs/:id](/doc/rest/p_domain/config/delete/pairs/p_id)
* [DELETE :domain/config/pairs/:pairKey/repair-actions/:name](/doc/rest/p_domain/config/delete/pairs/p_pairKey/repair-actions/p_name)
* [GET :domain/config/pairs/:id/repair-actions](/doc/rest/p_domain/config/get/pairs/p_id/repair-actions)
* [DELETE :domain/config/pairs/:pairKey/escalations/:name](/doc/rest/p_domain/config/delete/pairs/p_pairKey/escalations/p_name)
* [POST :domain/config/members/:username](/doc/rest/p_domain/config/post/members/p_username)
* [DELETE :domain/config/members/:username](/doc/rest/p_domain/config/delete/members/p_username)
* [GET :domain/config/endpoints/:id](/doc/rest/p_domain/config/get/endpoints/p_id)
* [POST :domain/config/endpoints](/doc/rest/p_domain/config/post/endpoints)
* PUT :domain/config/endpoints/:id
* [POST :domain/config/pairs](/doc/rest/p_domain/config/post/pairs)
* [PUT :domain/config/pairs/:id](/doc/rest/p_domain/config/put/pairs/p_id)
* [POST :domain/config/pairs/:id/repair-actions](/doc/rest/p_domain/config/post/pairs/p_id/repair-actions)
* [POST :domain/config/pairs/:id/escalations](/doc/rest/p_domain/config/post/pairs/p_id/escalations)

### ScanningResource

* [GET :domain/scanning/states](/doc/rest/p_domain/scanning/get/states)
* [POST :domain/scanning/pairs/:pairKey/scan](/doc/rest/p_domain/scanning/post/pairs/p_pairKey/scan)
* [POST :domain/scanning/scan_all](/doc/rest/p_domain/scanning/post/scan_all)
* [DELETE :domain/scanning/pairs/:pairKey/scan](/doc/rest/p_domain/scanning/delete/pairs/p_pairKey/scan)

### DiagnosticsResource

* [GET :domain/diagnostics/:pairKey/log](/doc/rest/p_domain/diagnostics/get/p_pairKey/log)

### DifferencesResource

* [GET :domain/diffs/events/:evtSeqId/:participant](/doc/rest/p_domain/diffs/get/events/p_evtSeqId/p_participant)
* [DELETE :domain/diffs/events/:evtSeqId](/doc/rest/p_domain/diffs/delete/events/p_evtSeqId)
* [PUT :domain/diffs/events/:evtSeqId](/doc/rest/p_domain/diffs/put/events/p_evtSeqId)
* [GET :domain/diffs/tiles/:zoomLevel](/doc/rest/p_domain/diffs/get/tiles/p_zoomLevel)

### ActionsResource

* [GET :domain/actions/:pairId](/doc/rest/p_domain/actions/get/p_pairId)
* [POST :domain/actions/:pairId/:actionId](/doc/rest/p_domain/actions/post/p_pairId/p_actionId)
* [POST :domain/actions/:pairId/:actionId/:entityId](/doc/rest/p_domain/actions/post/p_pairId/p_actionId/p_entityId)

### UsersResource

* [GET security/users](/doc/rest/security/get/users)
* [GET security/users/:name](/doc/rest/security/get/users/p_name)
* [DELETE security/users/:name](/doc/rest/security/delete/users/p_name)
* [POST security/users](/doc/rest/security/post/users)
* [PUT security/users/:name](/doc/rest/security/put/users/p_name)

### EscalationsResource

* [GET :domain/escalations/:pairId](/doc/rest/p_domain/escalations/get/p_pairId)

### SystemConfigResource

* [DELETE root/domains/:name](/doc/rest/root/delete/p_domains/p_name)
* [POST root/domains](/doc/rest/root/post/p_domains)


</div>

<div id="resources" markdown="1">
PUT :domain/config/endpoints/:id
=======================================================

<em>Updates the attributes of an endpoint that is registered with the agent.</em>

Entity Type
-----------
EndpointDef

URL
---
http://server:port/diffa-agent/rest/:domain/config/endpoints/:id

 
Mandatory Parameters
--------------------

### id

*string*

Endpoint ID

Example
-------</div>
<div id="example">
<pre class="brush: js">{
  "name" : "upstream-system",
  "contentType" : "application/json",
  "categories" : {
    "bizDate" : {
      "@type" : "range",
      "maxGranularity" : null,
      "dataType" : "datetime",
      "lower" : null,
      "upper" : null,
      "id" : 0
    }
  },
  "inboundContentType" : "application/json",
  "inboundUrl" : "http://diff.io/domain/changes",
  "versionGenerationUrl" : null,
  "contentRetrievalUrl" : "http://acme.com/upstream/node-content",
  "scanUrl" : "http://acme.com/upstream/scan"
}</pre>
</div>