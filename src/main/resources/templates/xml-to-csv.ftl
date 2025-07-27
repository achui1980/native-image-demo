<#if records?has_content>
<#-- 获取第一条记录的所有字段名 -->
<#list records[0]?keys as key>${key}<#if key_has_next>,</#if></#list>
<#-- 输出每条记录的值 -->
<#list records as record>
<#list record?keys as key>${record[key]}<#if key_has_next>,</#if></#list>
</#list>
</#if>