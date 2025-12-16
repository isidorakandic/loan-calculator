<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:uml="http://www.omg.org/spec/UML/20131001"
  xmlns:xmi="http://www.omg.org/XMI">
  <xsl:output method="html" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:key name="elementById" match="*[@xmi:id]" use="@xmi:id"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>Loan Calculator UML Diagram</title>
        <style>
          body { font-family: "Segoe UI", Arial, sans-serif; margin: 2rem; background: #f8fafc; color: #0f172a; }
          h1 { font-size: 1.8rem; margin-bottom: 0.25rem; }
          h2 { margin-top: 1.75rem; border-bottom: 2px solid #cbd5e1; padding-bottom: 0.25rem; }
          .panel { background: #fff; border: 1px solid #e2e8f0; border-radius: 8px; padding: 1rem 1.25rem; box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08); }
          .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(260px, 1fr)); gap: 1rem; }
          .card { border: 1px solid #e2e8f0; border-radius: 6px; padding: 0.75rem 1rem; background: linear-gradient(180deg, #fff, #f8fafc); }
          .card h3 { margin-top: 0; margin-bottom: 0.35rem; font-size: 1.1rem; }
          .muted { color: #64748b; font-size: 0.9rem; }
          ul { padding-left: 1.25rem; margin: 0.3rem 0 0.75rem; }
          li { margin-bottom: 0.35rem; }
          .badge { display: inline-block; padding: 0.1rem 0.4rem; border-radius: 999px; background: #e2e8f0; color: #0f172a; font-size: 0.75rem; margin-left: 0.35rem; }
          table { width: 100%; border-collapse: collapse; }
          th, td { padding: 0.35rem 0.5rem; border-bottom: 1px solid #e2e8f0; text-align: left; }
          th { background: #f1f5f9; font-weight: 600; }
        </style>
      </head>
      <body>
        <h1>Loan Calculator UML Diagram</h1>
        <p class="muted">Rendered from the XMI source. Open this file directly in a browser to see the structured view.</p>
        <xsl:if test="//uml:Model">
          <xsl:call-template name="classDiagram"/>
        </xsl:if>
        <xsl:if test="//uml:Interaction">
          <xsl:call-template name="sequenceDiagram"/>
        </xsl:if>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="classDiagram">
    <h2>Class Diagram</h2>
    <div class="panel">
      <div class="grid">
        <xsl:for-each select="//uml:Model//uml:Class">
          <div class="card">
            <h3>
              <xsl:value-of select="@name"/>
            </h3>
            <xsl:if test="@isAbstract='true'">
              <span class="badge">abstract</span>
            </xsl:if>
            <xsl:if test="uml:ownedAttribute">
              <div class="muted">Attributes</div>
              <ul>
                <xsl:for-each select="uml:ownedAttribute">
                  <li>
                    <strong><xsl:value-of select="@name"/></strong>
                    <span class="badge"><xsl:value-of select="key('elementById', @type)/@name | @type"/></span>
                  </li>
                </xsl:for-each>
              </ul>
            </xsl:if>
            <xsl:if test="uml:ownedOperation">
              <div class="muted">Operations</div>
              <ul>
                <xsl:for-each select="uml:ownedOperation">
                  <li>
                    <strong><xsl:value-of select="@name"/></strong>
                    <xsl:if test="uml:ownedParameter">
                      (<xsl:for-each select="uml:ownedParameter[@direction='in']">
                        <xsl:value-of select="@name"/>
                        <span class="badge"><xsl:value-of select="key('elementById', @type)/@name | @type"/></span>
                        <xsl:if test="position()!=last()">, </xsl:if>
                      </xsl:for-each>)
                    </xsl:if>
                    <xsl:variable name="returnType" select="uml:ownedParameter[@direction='return'][1]"/>
                    <xsl:if test="$returnType">
                      : <span class="badge"><xsl:value-of select="key('elementById', $returnType/@type)/@name | $returnType/@type"/></span>
                    </xsl:if>
                  </li>
                </xsl:for-each>
              </ul>
            </xsl:if>
          </div>
        </xsl:for-each>
      </div>
      <xsl:if test="//uml:Association">
        <h3>Associations</h3>
        <table>
          <thead>
            <tr><th>From</th><th>To</th></tr>
          </thead>
          <tbody>
            <xsl:for-each select="//uml:Association">
              <tr>
                <td><xsl:value-of select="key('elementById', substring-before(@memberEnd, ' '))/@name | key('elementById', @memberEnd)/@name"/></td>
                <td><xsl:value-of select="key('elementById', substring-after(@memberEnd, ' '))/@name"/></td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
      </xsl:if>
    </div>
  </xsl:template>

  <xsl:template name="sequenceDiagram">
    <h2>Sequence Diagram</h2>
    <div class="panel">
      <xsl:variable name="interaction" select="//uml:Interaction"/>
      <xsl:if test="$interaction">
        <div class="muted">Interaction</div>
        <h3><xsl:value-of select="$interaction/@name"/></h3>
        <div class="muted">Lifelines</div>
        <ul>
          <xsl:for-each select="$interaction/*[local-name()='lifeline']">
            <li><strong><xsl:value-of select="@name"/></strong>
              <xsl:if test="@represents">
                <span class="badge">represents: <xsl:value-of select="key('elementById', @represents)/@name"/></span>
              </xsl:if>
            </li>
          </xsl:for-each>
        </ul>
        <div class="muted">Messages</div>
        <table>
          <thead>
            <tr><th>Name</th><th>Type</th><th>From</th><th>To</th></tr>
          </thead>
          <tbody>
            <xsl:for-each select="$interaction/*[local-name()='message']">
              <tr>
                <td><strong><xsl:value-of select="@name"/></strong></td>
                <td><span class="badge"><xsl:value-of select="@messageSort"/></span></td>
                <td><xsl:value-of select="key('elementById', @sendEvent)/@name"/></td>
                <td><xsl:value-of select="key('elementById', @receiveEvent)/@name"/></td>
              </tr>
            </xsl:for-each>
          </tbody>
        </table>
      </xsl:if>
    </div>
  </xsl:template>
</xsl:stylesheet>
