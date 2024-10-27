package main

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

type Test struct {
	url                 string
	expectedContentType string
	expectedStatusCode  int
}

func TestWebServer(t *testing.T) {
	tests := []Test{
		{"/", "text/html; charset=utf-8", http.StatusOK},
		{"/about", "text/html; charset=utf-8", http.StatusOK},
		{"/lauren", "text/html; charset=utf-8", http.StatusOK},
		{"/portfolio", "text/html; charset=utf-8", http.StatusOK},
		{"/404", "text/plain; charset=utf-8", http.StatusNotFound},
		{"/static/css/index.css", "text/css; charset=utf-8", http.StatusOK},
		{"/static/img/hhacks.png", "image/png", http.StatusOK},
		{"/static/js/portfolio.js", "text/javascript; charset=utf-8", http.StatusOK},
	}

	mux := getMux()

	for _, test := range tests {
		req, err := http.NewRequest(http.MethodGet, test.url, nil)

		if err != nil {
			t.Errorf("could not create request: %v", err)
		}

		res := httptest.NewRecorder()
		mux.ServeHTTP(res, req)

		expectedContentType := test.expectedContentType
		expectedStatusCode := test.expectedStatusCode

		actualContentType := res.Header().Get("Content-Type")
		actualStatusCode := res.Code

		if actualContentType != expectedContentType {
			t.Errorf("req: %v; expected content-type header: %s, actual content-type header: %s",
				req, expectedContentType, actualContentType)
		}

		if actualStatusCode != expectedStatusCode {
			t.Errorf("req: %v; expected status code: %d, actual status code: %d",
				req, expectedStatusCode, actualStatusCode)
		}
	}
}
