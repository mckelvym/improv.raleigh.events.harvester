# Improv Raleigh Events Harvester

This Java application scrapes comedy shows and events from [Improv Raleigh](https://improv.com/raleigh/calendar/) and generates an RSS feed. It uses Selenium WebDriver with headless Chrome to handle JavaScript-rendered content and JSoup for HTML parsing. The application produces an incremental RSS feed that appends new events to an existing feed file while filtering out old entries.

Feed exported to https://github.com/mckelvym/improv.raleigh.events.rss

## Build and Run

Build the application:

```bash
./gradlew build
```

Build the image:

```bash
source scripts/version.sh && ./gradlew jib -Djib.to.image=$IMAGE:$VERSION
```

Run with default output file (events.xml):

```bash
./gradlew run
```

Run with custom output file:

```bash
./gradlew run -Pargs='my-events.xml'
```

## Docker

The project uses Jib for containerization. Run with Docker:

```bash
./scripts/run.sh
```

This pulls and runs the latest Docker image from GitHub Container Registry.

## How It Works

The application follows a three-phase workflow:

1. Load Existing Feed - Reads the existing RSS file and extracts all GUIDs to avoid duplicates
2. Scrape Events - Uses Selenium to load the calendar page, follows pagination via "More Shows" button (up to 20 pages), and parses each event for title, date, description, and image information. Optionally fetches individual event pages for enhanced descriptions and high-quality images
3. Generate RSS Feed - Creates a new RSS 2.0 XML document with new events, imports existing events from the old feed, filters out events older than 7 days, and writes the result to the output file

The scraper handles both eagerly-loaded and lazy-loaded images (via data-src attributes).

## Architecture

The application uses a modular SOLID design with clear separation of concerns:

- Domain layer: EventItem for event data
- Config layer: Site-specific configuration
- WebDriver layer: Chrome automation and page loading
- Scraper layer: Event discovery and pagination
- Parser layer: Multi-strategy field extraction with optional enhanced detail fetching
- Feed layer: XXE-protected RSS generation

## Configuration

Event retention period: 7 days
Page load timeout: 10 seconds
Enhanced event details: Configurable (fetches individual event pages for fuller descriptions and high-quality images)
Target URL: https://improv.com/raleigh/calendar/

## Output

The generated RSS feed includes:

- Event title
- Event description (enhanced when configured)
- Event link (also used as GUID)
- Event image (embedded in description, high-quality when enhanced detail fetching is enabled)
- Date and show times
- Publication date

The scraper can optionally fetch individual event pages to extract enhanced descriptions and higher resolution images.
