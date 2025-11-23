package com.rockthejvm.staticsite

object BasicServer extends cask.MainRoutes {
  override val port = 4321

  ExpandedWebsite.renderBlog()
  val resourcePath = os.Path("C:\\Users\\lanfr\\IdeaProjects\\scala-projects\\staticsite\\src\\main\\resources")
  val blogDir = resourcePath / "blog_expanded_out"

  @cask.staticFiles("/")
  def staticFileRoutes() = blogDir.toString

  // start the server
  initialize()
}
