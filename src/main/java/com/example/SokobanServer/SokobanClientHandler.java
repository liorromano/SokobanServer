package com.example.SokobanServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.example.server.model.AdminModel;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import model.LevelBin;



public class SokobanClientHandler implements ClientHandler {

	@Override
	public void handleClient(Socket socket, InputStream in, OutputStream out) {
		ObjectInputStream ois = null;
		PrintWriter writer = null;
		try {
			ois = new ObjectInputStream(in);
			writer = new PrintWriter(out);

			LevelBin game = (LevelBin)ois.readObject();
			String name = game.getName();
			String userName = game.getUserName();
			String level=game.getLevelString();

			AdminModel.getInstance().addClient(userName, socket);

			String solution = getSolutionFromService(name,level);
			if (solution == null) {
				// call Strips ...
				// save the solution via the service
			}
			writer.println(solution);
			writer.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

		private String getSolutionFromService(String name,String levelString) {
			String solution="";
			String url = "http://localhost:8080/WebService/webapi/solutions/" + name;
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(url);
			Response response = webTarget.request(MediaType.TEXT_PLAIN).get(Response.class);
			if (response.getStatus() == 200) {
				solution = response.readEntity(String.class);
				System.out.println("solution: " + solution);
				return solution;
			}
			com.sun.jersey.api.client.Client webClient = com.sun.jersey.api.client.Client.create();
			WebResource webResource = webClient.resource(url);
			ClientResponse response2 = webResource.type("text/plain").post(ClientResponse.class, levelString);

			if (response2.getStatus() == 201) {
				solution = response2.getEntity(String.class);
				System.out.println("solution: " + solution);
				return solution;
			}
			else if(solution.equals("Unsolvable")) {
				System.out.println("Unable to solve level "+name);
			}
			return null;
		}

}
