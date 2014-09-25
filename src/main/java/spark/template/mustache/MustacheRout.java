package spark.template.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import spark.ModelAndView;
import spark.TemplateViewRoute;

import java.io.IOException;
import java.io.StringWriter;


/**
 * Renders HTML from Route output using Mustache.
 *
 * FreeMarker configuration can be set with the {@link MustacheRout#setMustacheFactory(com.github.mustachejava.MustacheFactory)}
 * method. If no configuration is set the default configuration will be used where
 * ftl files need to be put in directory spark/template/freemarker under the resources directory.
 *
 * @author ryber
 * @author Alex
 * @author Per Wendel
 */
public abstract class MustacheRout implements TemplateViewRoute {

    /** The FreeMarker configuration */
    MustacheFactory mustacheFactory;

    /**
     * Creates a FreeMarkerRoute for a path
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name)
     */
    protected MustacheRout(String path) {
        this.mustacheFactory = createDefaultConfiguration();
    }

    /**
     * Creates a FreeMarkerRoute for a path and accept type
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name)
     * @param acceptType The accept type which is used for matching.
     */
    protected MustacheRout(String path, String acceptType) {
        this.mustacheFactory = createDefaultConfiguration();
    }

    /**
     * Creates a FreeMarkerRoute for a path with a configuration
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name)
     * @param mustacheFactory The Mustache Factory
     */
    protected MustacheRout(String path, MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    /**
     * Creates a FreeMarkerRoute for a path and accept type with a configuration
     *
     * @param path The route path which is used for matching. (e.g. /hello, users/:name)
     * @param acceptType The accept type which is used for matching.
     * @param mustacheFactory The Mustache Factory
     */
    protected MustacheRout(String path, String acceptType, MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    public String render(ModelAndView modelAndView) {
        Mustache mustache = mustacheFactory.compile(modelAndView.getViewName());

        try {
            StringWriter sw = new StringWriter();
            mustache.execute(sw, modelAndView.getModel()).flush();
            return sw.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Sets Mustache factory.
     * Note: If configuration is not set the default configuration
     * will be used.
     *
     * @param mustacheFactory the MustacheFactory to set
     */
    public void setMustacheFactory(MustacheFactory mustacheFactory) {
        this.mustacheFactory = mustacheFactory;
    }

    private MustacheFactory createDefaultConfiguration() {
        return new DefaultMustacheFactory("spark/template/mustache/");
    }
}