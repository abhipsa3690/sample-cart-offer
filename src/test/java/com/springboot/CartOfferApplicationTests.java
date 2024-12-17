package com.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.ApplyOfferRequest;
import com.springboot.controller.ApplyOfferResponse;
import com.springboot.controller.OfferRequest;
import com.springboot.controller.SegmentResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartOfferApplicationTests {

	// Test Case 1: Verify applying a FLATX offer for a "p2" segment
	@Test
	public void checkFlatXForP2Segment() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p2");  // Add the segment "p2"

		// Create an OfferRequest with FLATX offer type and 10% discount
		OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);

		// Call the addOffer method and check if the offer is successfully added
		boolean result = addOffer(offerRequest);
		Assert.assertTrue(result); // Verify offer is added successfully
	}

	// Test Case 2: Verify applying a FLAT% offer for a "p1" segment
	@Test
	public void checkFlatPercentForP1Segment() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");  // Add the segment "p1"

		// Create an OfferRequest with FLAT% offer type and 10% discount
		OfferRequest offerRequest = new OfferRequest(1, "FLAT%", 10, segments);

		// Call the addOffer method and check if the offer is successfully added
		boolean result = addOffer(offerRequest);
		Assert.assertTrue(result); // Verify offer is added successfully
	}

	// Test Case 3: Verify applying an offer for multiple segments (p1 and p2)
	@Test
	public void checkMultipleSegments() throws Exception {
		List<String> segments = new ArrayList<>();
		segments.add("p1");  // Add "p1" segment
		segments.add("p2");  // Add "p2" segment

		// Create an OfferRequest with FLAT% offer type and 15% discount for multiple segments
		OfferRequest offerRequest = new OfferRequest(1, "FLAT%", 15, segments);

		// Call the addOffer method and check if the offer is successfully added
		boolean result = addOffer(offerRequest);
		Assert.assertTrue(result); // Verify offer is added successfully
	}

	// Test Case 4: Apply Offer for Multiple Scenarios
	@Test
	public void checkApplyOfferForMultipleScenarios() throws Exception {
		// Scenario 1: Apply offer for p1 segment with FLAT% offer
		ApplyOfferRequest applyOfferRequest1 = new ApplyOfferRequest();
		applyOfferRequest1.setCart_value(100); // Initial cart value
		applyOfferRequest1.setRestaurant_id(1);
		applyOfferRequest1.setUser_id(1);

		ApplyOfferResponse response1 = applyOffer(applyOfferRequest1);

		// Assert the response is not null and verify the cart value after applying the offer
		Assert.assertNotNull(response1);
		Assert.assertEquals(85, response1.getCart_value()); // Verify that the offer applied (100 - 10% = 90)

		// Scenario 2: Apply offer for p2 segment with FLATX offer
		ApplyOfferRequest applyOfferRequest2 = new ApplyOfferRequest();
		applyOfferRequest2.setCart_value(200); // Initial cart value
		applyOfferRequest2.setRestaurant_id(1);
		applyOfferRequest2.setUser_id(2);

		ApplyOfferResponse response2 = applyOffer(applyOfferRequest2);

		// Assert the response is not null and verify the cart value after applying the offer
		Assert.assertNotNull(response2);
		Assert.assertEquals(190, response2.getCart_value()); // Verify that the offer applied (200 - 10% = 180)

		// Scenario 3: Apply offer for invalid segment (edge case)
		ApplyOfferRequest applyOfferRequest3 = new ApplyOfferRequest();
		applyOfferRequest3.setCart_value(300); // Initial cart value
		applyOfferRequest3.setRestaurant_id(1);
		applyOfferRequest3.setUser_id(3);

		ApplyOfferResponse response3 = applyOffer(applyOfferRequest3);

		// Assert the response is not null and verify the cart value remains unchanged for invalid segment
		Assert.assertNotNull(response3);
		Assert.assertEquals(300, response3.getCart_value()); // No offer applied, cart value remains the same
	}



	// Helper Method 1: Adds an offer using HTTP POST request
	private boolean addOffer(OfferRequest offerRequest) throws Exception {
		String urlString = "http://localhost:9001/api/v1/offer";  // URL for the offer API
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setDoOutput(true);  // Set the connection to allow output
		con.setRequestProperty("Content-Type", "application/json");  // Set content type as JSON

		// Convert OfferRequest to JSON string
		ObjectMapper mapper = new ObjectMapper();
		String POST_PARAMS = mapper.writeValueAsString(offerRequest);

		// Send the HTTP POST request with the offer details
		OutputStream os = con.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();

		// Get the response code from the server
		int responseCode = con.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) {  // Verify success response
			// Read the server's response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println("Offer added: " + response.toString());  // Print response for debugging
			return true;
		} else {
			System.out.println("Failed to add offer.");  // Print failure message for debugging
			return false;
		}
	}

	// Helper Method 2: Applies an offer to a cart using HTTP POST request
	private ApplyOfferResponse applyOffer(ApplyOfferRequest applyOfferRequest) throws Exception {
		String urlString = "http://localhost:9001/api/v1/cart/apply_offer";  // URL for the apply offer API
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setDoOutput(true);  // Set the connection to allow output
		con.setRequestProperty("Content-Type", "application/json");  // Set content type as JSON

		// Convert ApplyOfferRequest to JSON string
		ObjectMapper mapper = new ObjectMapper();
		String POST_PARAMS = mapper.writeValueAsString(applyOfferRequest);

		// Send the HTTP POST request with the cart details
		OutputStream os = con.getOutputStream();
		os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();

		// Get the response code from the server
		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {  // Verify success response
			// Read the server's response
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// Convert response JSON to ApplyOfferResponse and return it
			return mapper.readValue(response.toString(), ApplyOfferResponse.class);
		} else {
			System.out.println("Failed to apply offer.");  // Print failure message for debugging
			return null;
		}
	}
}
