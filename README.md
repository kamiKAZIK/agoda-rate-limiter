# agoda-rate-limiter

Web server is launched using this class: com.agoda.eduardas.hotel.rest.handling.WebServer

Example requests:

Example 1:
  URL:      http://localhost:9090/hotel?city=Bangkok
  Header:   X-Api-Token: token-1

Example 2:
  URL:      http://localhost:9090/hotel?city=Bangkok&sorting=asc
  Header:   X-Api-Token: token-2

Example 3:
  URL:      http://localhost:9090/hotel?city=Bangkok&sorting=desc
  Header:   X-Api-Token: token-3