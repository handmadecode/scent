<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text"/>
    <xsl:template match="scent-report">
        Date: <xsl:value-of select="@date"/>
        Time: <xsl:value-of select="@time"/>
        Version: <xsl:value-of select="@version"/>
        <xsl:for-each select="packages/package">
            Pkg: <xsl:value-of select="@name"/>
        </xsl:for-each>
        <xsl:for-each select="packages/package/compilation-units/compilation-unit/types/type">
            Type: <xsl:value-of select="@name"/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
