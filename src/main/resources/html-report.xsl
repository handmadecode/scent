<?xml version="1.0"?>
<!--
 *******************************************************************************
 *
 * XSL style sheet for transforming a Scent XML report into an HTML page.
 *
 *******************************************************************************
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>

    <!-- Template for the scent-report element, which is the top-level element in the Scent report -->
    <xsl:template match="scent-report">
       <html>
           <!-- HTML header with title and style sheet -->
           <head>
               <title>Scent report<xsl:text>&#32;</xsl:text>
                   <xsl:value-of select="@date"/><xsl:text>&#32;</xsl:text>
                   <xsl:value-of select="@time"/>
               </title>
               <xsl:call-template name="output-css" />
           </head>

           <body>
               <div class="mainsection">
                   <!-- Report header -->
                   <div class="mainheader">Scent Report</div>
                   <!-- Timestamp and version -->
                   <xsl:call-template name="output-timestamp-and-version"/>
                   <!-- Report sections for applicable child elements -->
                   <xsl:apply-templates/>
               </div>
           </body>
       </html>
    </xsl:template>


    <!-- Template for the summary element, outputs a table with each summary attribute -->
    <xsl:template match="summary">
        <div class="level1section">
            <div class="level1header">Summary</div>
            <table class="level1sectionitem" width="100%" cellpadding="2" cellspacing="0" border="0">
                <colgroup>
                    <col width="12%"/>
                    <col width="3%"/>
                    <col width="6%"/>
                    <col width="12%"/>
                    <col width="3%"/>
                    <col width="6%"/>
                    <col width="12%"/>
                    <col width="3%"/>
                    <col width="6%"/>
                    <col width="12%"/>
                    <col width="3%"/>
                    <col width="7%"/>
                    <col width="12%"/>
                    <col width="3%"/>
                </colgroup>
                <tr>
                    <td class="label">Modules:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@modules"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">Types:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@types"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">Statements:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@statements"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">JavaDocs:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@javadocs"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">Block comments:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@block-comments"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <td class="label">Packages:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@packages"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">Methods:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@methods"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">Line comments:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@line-comments"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">JavaDoc lines:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@javadoc-lines"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">Block comment lines:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@block-comments-lines"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <td class="label">Compilation Units:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@compilation-units"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">Fields:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@fields"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">Line comments length:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@line-comments-length"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">JavaDocs length:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@javadocs-length"/>
                    </xsl:call-template>
                    <td/>
                    <td class="label">Block comments length:</td>
                    <xsl:call-template name="output-value-cell">
                        <xsl:with-param name="value" select="@block-comments-length"/>
                    </xsl:call-template>
                </tr>
            </table>
        </div>
    </xsl:template>


    <!-- Template for the packages element, outputs a table with metrics for each package -->
    <xsl:template match="packages">
        <div class="level1section">
            <div class="level1header">Packages</div>
            <table class="level1sectionitem" width="100%" cellpadding="2" cellspacing="0" border="0">
                <!-- Table header with column names -->
                <xsl:call-template name="output-name-and-summary-table-header"/>
                <!-- One table row per package, sorted by name -->
                <xsl:for-each select="package">
                    <xsl:sort select="@name"/>
                    <xsl:call-template name="output-name-and-summary-table-row"/>
                </xsl:for-each>
            </table>

            <!-- Details for each package, sorted by name -->
            <xsl:for-each select="package">
                <xsl:sort select="@name"/>
                <xsl:apply-templates select="."/>
            </xsl:for-each>
        </div>
    </xsl:template>


    <!-- Template for the package element, outputs a section with a table with metrics for each
         compilation unit -->
    <xsl:template match="package">
        <div class="level2section">
            <div class="level2header"><xsl:value-of select="@name"/></div>
            <table class="level2sectionitem" width="100%" cellpadding="2" cellspacing="0" border="0">
                <!-- Table header with column names -->
                <xsl:call-template name="output-name-and-summary-table-header">
                    <xsl:with-param name="name-header">
                        <xsl:value-of select="'Compilation unit'"/>
                    </xsl:with-param>
                </xsl:call-template>
                <!-- One table row per compilation unit, sorted by name -->
                <xsl:for-each select="compilation-units/compilation-unit">
                    <xsl:sort select="@name"/>
                    <xsl:call-template name="output-name-and-summary-table-row"/>
                </xsl:for-each>
            </table>
        </div>
    </xsl:template>


    <!-- Output a table with the timestamp and version -->
    <xsl:template name="output-timestamp-and-version">
        <table class="mainsectionitem" width="50%" cellpadding="2" cellspacing="0" border="0">
            <tr>
                <td class="data">
                    Timestamp:
                    <xsl:text>&#32;</xsl:text>
                    <xsl:value-of select="@date"/>
                    <xsl:text>&#32;</xsl:text>
                    <xsl:value-of select="@time"/>
                </td>
            </tr>
            <tr>
                <td class="data">
                    Version:
                    <xsl:text>&#32;</xsl:text>
                    <xsl:value-of select="@version"/>
                </td>
            </tr>
        </table>
    </xsl:template>


    <!-- Output a table header for the name attribute and the summary child element's attributes -->
    <xsl:template name="output-name-and-summary-table-header">
        <xsl:param name="name-header" select="'Name'"/>
        <colgroup>
            <col width="16%"/>
            <col width="7%"/><col width="7%"/><col width="7%"/>
            <col width="7%"/><col width="7%"/><col width="7%"/>
            <col width="7%"/><col width="7%"/><col width="7%"/>
            <col width="7%"/><col width="7%"/><col width="7%"/>
        </colgroup>
        <tr>
            <td/>
            <td/>
            <td/>
            <td/>
            <td/>
            <td class="colheader" align="right" colspan="2">Line comments</td>
            <td/>
            <td class="colheader" align="right">JavaDocs</td>
            <td/>
            <td class="colheader" align="right" colspan="2">Block comments</td>
            <td/>
        </tr>
        <tr>
            <td class="colheader"><xsl:value-of select="$name-header"/></td>
            <td class="colheader" align="right">Types</td>
            <td class="colheader" align="right">Methods</td>
            <td class="colheader" align="right">Fields</td>
            <td class="colheader" align="right">Statements</td>
            <td class="colheader" align="right">count</td>
            <td class="colheader" align="right">length</td>
            <td class="colheader" align="right">count</td>
            <td class="colheader" align="right">lines</td>
            <td class="colheader" align="right">length</td>
            <td class="colheader" align="right">count</td>
            <td class="colheader" align="right">lines</td>
            <td class="colheader" align="right">length</td>
        </tr>
    </xsl:template>


    <!-- Output a table row with the name attribute and the summary child element's attributes -->
    <xsl:template name="output-name-and-summary-table-row">
        <tr>
            <!-- Use alternate row characteristics every other row -->
            <xsl:if test="position() mod 2 = 0">
                <xsl:attribute name="class">altrow</xsl:attribute>
            </xsl:if>
            <td class="data"><xsl:value-of select="@name"/></td>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@types"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@methods"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@fields"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@statements"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@line-comments"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@line-comments-length"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@javadocs"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@javadoc-lines"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@javadocs-length"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@block-comments"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@block-comments-lines"/>
            </xsl:call-template>
            <xsl:call-template name="output-value-cell">
                <xsl:with-param name="value" select="summary/@block-comments-length"/>
            </xsl:call-template>
        </tr>
    </xsl:template>


    <!-- Output a table cell with a value if it exists, otherwise a marker value for non-presence -->
    <xsl:template name="output-value-cell">
        <xsl:param name="value"/>
        <td class="data" align="right">
            <xsl:choose>
                <xsl:when test="$value">
                    <xsl:value-of select="$value" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>0</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </td>
    </xsl:template>


    <!-- Output a style element with the CSS -->
    <xsl:template name="output-css">
        <style type="text/css">
            body
            { font-family:arial,helvetica,sans-serif; color:#000000; }
            .headline
            { font-size:16pt; font-weight:bold;
              margin: 20px 0px 10px 0px; padding: 2px 10px 2px 10px; }

            .summarysection
            { margin: 15px 10px; }
            .summarysectionitem
            { padding: 5px 5px 10px 5px; vertical-align: top; }
            .summaryheader
            { background-color:#000000; border-radius: 10px;
            font-size:14pt; font-weight:bold; color:#ffffff;
            padding: 2px 10px; margin-bottom: 5px; }
            .summaryintro
            { font-size: 9pt; }
            .summarysectionitem table
            { width: 100%; border-radius: 10px; border-collapse: collapse; }
            .summarysectionitem td
            { text-align: center; vertical-align: top; padding: 4px 10px; }
            .summarylabel
            { font-size: 10pt; font-style:oblique; }
            .summaryvalue
            { font-size: 14pt; font-weight:bold; }

            .mainsection
            { margin: 30px 10px 20px 10px; }
            .mainsectionitem
            { padding: 10px 10px 2px 10px; }
            .mainheader
            { background-color:#000000;
            font-size:14pt; font-weight:bold; color:#ffffff;
            padding: 2px 10px; }

            .level1section
            { margin: 10px 20px 5px 10px; }
            .level1sectionitem
            { padding: 2px 10px; }
            .level1header
            { background-color:#000066;
            font-size:11pt; font-weight:bold; color:#ffffff;
            padding: 2px 10px; }

            .level2section
            { margin: 5px 25px; }
            .level2sectionitem
            { padding: 2px 10px; }
            .level2header
            { background-color:#c0e6ff;
            font-size:9pt; font-weight:bold;
            padding: 2px 10px; margin: 10px 5px 2px 5px; }

            .colheader
            { font-size:8pt; font-weight:bold; vertical-align: top; }

            .label
            { font-size: 8pt; font-style: oblique; }
            .data
            { font-size:8pt; }

            .neutralbg
            { background-color: #a7cdff; }
            .successbg
            { background-color: #b7ffa6; }
            .warning
            { color: #ff6600; }
            .warningbg
            { background-color: #ffcd80; }
            .error
            { color: #ff0000; }
            .errorbg
            { background-color: #ffc4c4; }

            .altrow
            { background-color:#eeeeee }
        </style>
    </xsl:template>

</xsl:stylesheet>
