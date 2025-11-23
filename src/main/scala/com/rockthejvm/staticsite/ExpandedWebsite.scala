package com.rockthejvm.staticsite

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import os.{rel as _, *}
import scalatags.Text.all.*
import scalatags.Text.tags2

import java.time.{Instant, LocalDate, ZoneOffset}

case class ArticleMeta(slug: String, name: String, date: LocalDate)

object ExpandedWebsite {

  // Bootstrap CDN + a few small utilities
  val bootstrap = link(
    rel := "stylesheet",
    href := "https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
  )

  // Custom CSS that styles common Markdown constructs for a modern programmer blog
  private val customCss =
    """
      |:root {
      |  --content-max-width: 900px;
      |  --muted: #6c757d;
      |  --accent: #0d6efd;
      |  --code-bg: #0f1720;
      |  --code-color: #e6edf3;
      |  --card-bg: #ffffff;
      |}
      |body { background: #f8fafc; color: #0f1720; }
      |header.navbar { box-shadow: 0 2px 6px rgba(15,23,32,0.06); }
      |.site-container { max-width: var(--content-max-width); margin: 0 auto; }
      |.markdown-body { padding: 1rem 0; background: transparent; }
      |.markdown-body h1, .markdown-body h2, .markdown-body h3 { margin-top: 1.25rem; margin-bottom: .5rem; }
      |.markdown-body p { line-height: 1.7; margin-bottom: .9rem; }
      |.markdown-body img { max-width: 100%; height: auto; display: block; margin: 0.75rem 0; border-radius: 6px; }
      |pre, code { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, "Roboto Mono", monospace; }
      |pre { background: var(--code-bg); color: var(--code-color); padding: 1rem; border-radius: 8px; overflow-x: auto; }
      |code { background: rgba(27,31,35,0.05); padding: .15rem .35rem; border-radius: 6px; }
      |table { width: 100%; border-collapse: collapse; margin-bottom: 1rem; }
      |table th, table td { border: 1px solid #e6edf3; padding: .5rem .75rem; text-align: left; }
      |blockquote { border-left: 4px solid var(--accent); background: rgba(13,110,253,0.04); padding: .5rem 1rem; color: var(--muted); border-radius: 4px; }
      |.post-meta { color: var(--muted); font-size: .9rem; margin-bottom: .75rem; }
      |footer.site-footer { color: var(--muted); padding: 2rem 0; text-align: center; font-size: .9rem; }
      |.card-post { background: var(--card-bg); border-radius: 8px; padding: 1rem; box-shadow: 0 4px 12px rgba(2,6,23,0.04); }
      |@media (max-width: 576px) { .site-container { padding: 0 1rem; } h1 { font-size: 1.4rem; } }
      |""".stripMargin
  private val hljs = script(src := "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/highlight.min.js")
  private val hljsCss = link(
    rel := "stylesheet",
    href := "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/default.min.css"
  )
  private val hljsAtomCss = link(
    rel := "stylesheet",
    href := "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/styles/atom-one-dark.min.css"
  )
  private val hljsTrigger = raw(
    """<script>document.addEventListener("DOMContentLoaded", (event) => {
      |  document.querySelectorAll('pre code').forEach((block) => {
      |    hljs.highlightElement(block);
      |  });
      |});</script>""".stripMargin
  )
  private val hljsScala = script(
    src := "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.11.1/languages/scala.min.js"
  )

  private val parser = Parser.builder().build()
  private val renderer = HtmlRenderer.builder().build()

  def navbar: Frag = header(cls := "navbar navbar-expand-lg navbar-light bg-white",
    div(cls := "container site-container",
      a(cls := "navbar-brand me-3", href := "index.html", b("Programmer Blog")),
      button(
        cls := "navbar-toggler",
        `type` := "button",
        scalatags.Text.all.attr("data-bs-toggle") := "collapse",
        scalatags.Text.all.attr("data-bs-target") := "#navMenu",
        span(cls := "navbar-toggler-icon")
      ),
      div(cls := "collapse navbar-collapse", id := "navMenu",
        ul(cls := "navbar-nav ms-auto mb-2 mb-lg-0",
          li(cls := "nav-item", a(cls := "nav-link", href := "index.html", "Blog")),
          li(cls := "nav-item", a(cls := "nav-link", href := "projects.html", "Projects")),
          li(cls := "nav-item", a(cls := "nav-link", href := "about.html", "About")),
          li(cls := "nav-item", a(cls := "nav-link", href := "resume.html", "Resume")),
          li(cls := "nav-item", a(cls := "nav-link", href := "contact.html", "Contact"))
        )
      )
    )
  )

  // renamed to avoid collision with scalatags footer tag
  def siteFooter: Frag = footer(cls := "site-footer",
    div(cls := "container site-container",
      p(s"© ${java.time.Year.now().getValue} — Built with Scala • Designed for developers"),
      p(a(href := "https://github.com/", "GitHub"), " • ", a(href := "mailto:you@example.com", "Email"))
    )
  )

  // Generic page template with responsive site container and styles
  def pageTemplate(titleStr: String, contents: Frag): String =
    "<!doctype html>" +
      html(
        head(
          meta(charset := "utf-8"),
          meta(name := "viewport", content := "width=device-width, initial-scale=1"),
          tag("title")(titleStr),
          bootstrap,
          raw(s"<style>$customCss</style>"),
          hljsAtomCss
        ),
        body(
          navbar,
          tags2.main(cls := "container site-container mt-4",
            div(cls := "row",
              div(cls := "col-12",
                h1(cls := "mb-3", titleStr),
                div(cls := "markdown-body", contents)
              )
            )
          ),
          siteFooter,
          // small bootstrap bundle for responsive navbar toggling
          raw("""<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>"""),
          hljs,
          hljsScala,
          hljsTrigger
        )
      ).render

  // Render markdown string to HTML
  def renderMarkdownToHtml(md: String): String = {
    val ast = parser.parse(md)
    renderer.render(ast)
  }

  // Copy assets (images, etc.) from resources/assets to output folder if present
  private def copyAssets(resourceRoot: Path, outRoot: Path): Unit = {
    val assetsDir = resourceRoot / "assets"
    if (os.exists(assetsDir)) {
      os.makeDir.all(outRoot / "assets")
      os.list(assetsDir).foreach { p =>
        val dest = outRoot / "assets" / p.last
        os.copy.over(p, dest)
      }
    }
  }

  def renderPost(filepath: Path, outPath: Path): ArticleMeta = {
    val filename = filepath.last.stripSuffix(".md")
    val publishedDate = LocalDate.ofInstant(
      Instant.ofEpochMilli(os.mtime(filepath)),
      ZoneOffset.UTC
    )

    val md = os.read(filepath)
    val htmlOutput = renderMarkdownToHtml(md)

    val pageSlug = s"${filename.toLowerCase.replaceAll("\\s+", "-")}.html"

    val content = div(
      div(cls := "post-meta", s"Published: $publishedDate"),
      raw(htmlOutput)
    )

    os.write.over(
      outPath / "article" / pageSlug,
      pageTemplate(filename, content)
    )

    ArticleMeta(pageSlug, filename, publishedDate)
  }

  // Render a static site page (about/contact/projects/resume) from optional markdown
  def renderStaticPage(name: String, maybeMdPath: Option[Path], outPath: Path): Unit = {
    val title = name.capitalize
    val contentFrag = maybeMdPath match {
      case Some(mdPath) if os.exists(mdPath) =>
        raw(renderMarkdownToHtml(os.read(mdPath)))
      case _ =>
        div(cls := "card-post",
          p(s"This is the $title page. Add `src/main/resources/static/$name.md` to provide content."),
          p(a(href := "mailto:you@example.com", "Contact via email"))
        )
    }

    os.write.over(
      outPath / s"$name.html",
      pageTemplate(title, contentFrag)
    )
  }

  def renderBlog(): Unit = {
    val resourcePath = os.Path("C:\\Users\\lanfr\\IdeaProjects\\scala-projects\\staticsite\\src\\main\\resources")
    val blogRoot = resourcePath / "articles"
    val staticRoot = resourcePath / "pages"
    val outPath = resourcePath / "blog_expanded_out"

    // prepare output directories
    os.remove.all(outPath)
    os.makeDir.all(outPath / "article")

    // copy static assets (images, etc.)
    copyAssets(resourcePath, outPath)

    // articles
    val articles = if (os.exists(blogRoot)) {
      os.list(blogRoot)
        .filter(p => p.last.endsWith(".md"))
        .map(filePath => renderPost(filePath, outPath))
        .sortBy(_.date)(Ordering[LocalDate].reverse) // latest first
        .toList
    } else Nil

    // index (blog home) with article cards and excerpts
    val articlesListFrag =
      if (articles.isEmpty) p("No posts yet. Add markdown files to `src/main/resources/articles`.")
      else {
        div(
          articles.map {
            case ArticleMeta(slug, name, publishedDate) =>
              div(cls := "mb-3 card-post",
                h2(a(href := s"article/$slug", name)),
                div(cls := "post-meta", s"Published: $publishedDate"),
                p(a(href := s"article/$slug", "Read →"))
              )
          }
        )
      }

    os.write.over(
      outPath / "index.html",
      pageTemplate("Programmer Blog", div(h2("Latest Posts"), articlesListFrag))
    )

    // static pages to generate (look for corresponding markdown in resources/static)
    val staticPages = Seq("about", "contact", "projects", "resume")
    staticPages.foreach { pg =>
      val mdPath = staticRoot / s"$pg.md"
      renderStaticPage(pg, if (os.exists(mdPath)) Some(mdPath) else None, outPath)
    }
  }

  def main(args: Array[String]): Unit = {
    renderBlog()
  }
}