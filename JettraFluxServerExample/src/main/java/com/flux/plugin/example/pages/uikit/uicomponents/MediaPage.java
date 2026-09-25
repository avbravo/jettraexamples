package com.flux.plugin.example.pages.uikit.uicomponents;

import com.sun.net.httpserver.HttpExchange;
import io.jettra.flux.core.Widget;
import io.jettra.flux.core.Modifier;
import io.jettra.flux.widgets.*;
import io.jettra.core.security.widget.PageWidgetAllow;
import java.util.Map;
import com.flux.example.pages.template.TemplatePage;
import io.jettra.flux.sync.JettraPageSincronized;
import io.jettra.flux.sync.SyncType;

@JettraPageSincronized(SyncType.ALL)
@PageWidgetAllow(role = { jcf.AppRole.ADMIN, jcf.AppRole.MANAGER, jcf.AppRole.USER })
@io.jettra.core.server.Page(path = "/media")
public class MediaPage extends TemplatePage {

    @Override
    protected String getTitle() {
        return "Media - JettraFlux Pro";
    }

    @Override
    protected Widget buildCenter(HttpExchange exchange, Map<String, String> params, String currentTheme) {
        
        Widget galleriaCard = Panel.of("Galleria", 
            Galleria.of(
                "https://primefaces.org/cdn/primeng/images/galleria/galleria1.jpg",
                "https://primefaces.org/cdn/primeng/images/galleria/galleria2.jpg",
                "https://primefaces.org/cdn/primeng/images/galleria/galleria3.jpg",
                "https://primefaces.org/cdn/primeng/images/galleria/galleria4.jpg",
                "https://primefaces.org/cdn/primeng/images/galleria/galleria5.jpg"
            )
        );

        Widget imageCompareCard = Panel.of("Image Compare", 
            ImageCompare.of(
                "https://primefaces.org/cdn/primeng/images/galleria/galleria1.jpg",
                "https://primefaces.org/cdn/primeng/images/galleria/galleria2.jpg"
            )
        );

        Widget carouselCard = Panel.of("Carousel",
            Column.of(
                Header.of(5, "Horizontal"),
                Carousel.of(
                    Image.of("https://primefaces.org/cdn/primeng/images/product/bamboo-watch.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;")),
                    Image.of("https://primefaces.org/cdn/primeng/images/product/black-watch.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;")),
                    Image.of("https://primefaces.org/cdn/primeng/images/product/blue-band.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;")),
                    Image.of("https://primefaces.org/cdn/primeng/images/product/blue-t-shirt.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;")),
                    Image.of("https://primefaces.org/cdn/primeng/images/product/bracelet.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;"))
                ).modifier(new Modifier().style("padding: 20px;")),
                
                Header.of(5, "Vertical").modifier(new Modifier().style("margin-top:20px;")),
                Carousel.of(
                    Image.of("https://primefaces.org/cdn/primeng/images/product/bamboo-watch.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;")),
                    Image.of("https://primefaces.org/cdn/primeng/images/product/black-watch.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;")),
                    Image.of("https://primefaces.org/cdn/primeng/images/product/blue-band.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;")),
                    Image.of("https://primefaces.org/cdn/primeng/images/product/blue-t-shirt.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;")),
                    Image.of("https://primefaces.org/cdn/primeng/images/product/bracelet.jpg").modifier(new Modifier().style("width: 200px; height: 200px; object-fit: cover; border-radius: 8px;"))
                ).orientation("vertical").modifier(new Modifier().style("padding: 20px;"))
            ).modifier(new Modifier().style("width:100%;"))
        );

        Widget imageCard = Panel.of("Image",
            Image.of("https://primefaces.org/cdn/primeng/images/galleria/galleria1.jpg").modifier(new Modifier().style("width: 100%; max-width: 400px; border-radius: 8px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1);"))
        );

        return Column.of(
            Header.of(2, "Media").modifier(new Modifier().style("margin-top: 0; font-weight: 700; margin-bottom: 20px; color: var(--text-color);")),
            imageCard,
            galleriaCard,
            imageCompareCard,
            carouselCard
        ).modifier(new Modifier().style("width: 100%; align-items: stretch; max-width: 1200px; padding: 20px; gap: 20px;"));
    }
}
