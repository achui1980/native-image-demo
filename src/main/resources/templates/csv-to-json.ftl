[
<#list records as record>
    {
    <#list record?keys as key>
        "${key}": "${record[key]}"<#if key_has_next>,</#if>
    </#list>
    }<#if record_has_next>,</#if>
</#list>
]