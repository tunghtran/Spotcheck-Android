package com.spotcheck.api;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.spotcheck.entity.Account;
import com.spotcheck.entity.Spot;
import com.spotcheck.form.AccountForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Named;

import static com.spotcheck.service.OfyService.ofy;

/**
 * Defines spotcheck APIs.
 */
@Api(name = "spotcheck", version = "v1", namespace = @ApiNamespace(ownerDomain = "api.spotcheck.com", ownerName = "api.spotcheck.com"))
public class SpotcheckApi
{


	/*
	 * Get the display name from the user's email. For example, if the email is
	 * lemoncake@example.com, then the display name becomes "lemoncake."
	 */
	private static String extractDefaultDisplayNameFromEmail(String email)
	{
		return email == null ? null : email.substring(0, email.indexOf("@"));
	}

	/**
	 * Creates or updates an Account object associated with the given user
	 * object.
	 *
	 * @param accountForm
	 *            A AccountForm object sent from the client form.
	 * @return Account object just created.
	 * @throws UnauthorizedException
	 *             when the User object is null.
	 */
	@ApiMethod(name = "saveAccount", path = "saveAccount", httpMethod = HttpMethod.POST)
	public Account saveAccount(AccountForm accountForm) throws UnauthorizedException
	{
		//set account data from form input
		String firstName = accountForm.getFirstName();
		String lastName = accountForm.getLastName();
		String email = accountForm.getEmail();
		String password = accountForm.getPassword(); // TO DO: hash and salt

		//set account data
		Account account = getAccount(email);
		if (account == null)
			account = new Account(firstName, lastName, email, password);
		else
			account.update(firstName, lastName, email, password);

		//save the account in the datastore
		ofy().save().entity(account).now();

		return account;
	}

	/**
	 * Returns an Account object associated with the given user object.
	 *
	 * @return Account object.
	 * @param email the email of the account to retrieve
	 */
	public Account getAccount(@Named("email") String email)
	{
		// load an Account Entity
		Key key = Key.create(Account.class, email);
		Account account = (Account) ofy().load().key(key).now();
		return account;
	}

	/**
	 * Returns an Account object associated with the given user object.
	 *
	 * @return Account object.
	 * @throws UnauthorizedException
	 *             when the User object is null.
	 */
	@ApiMethod(name = "authenticateAccount", path = "authenticateAccount", httpMethod = HttpMethod.POST)
	public Account authenticateAccount(@Named("email") String email, @Named("password") String password) throws UnauthorizedException
	{
		// load an Account Entity
		Account account = getAccount(email);

		// Authenticate user
		String passwordHash = password; // TO DO: hash and salt
		if (account != null && account.getPassword().equals(passwordHash))
			return account;
		else return null;
	}

	@ApiMethod(name = "listSpot")
	public CollectionResponse<Spot> listSpot(@Nullable @Named("cursor") String cursorString,
											 @Nullable @Named("count") Integer count) {

		Query<Spot> query = ofy().load().type(Spot.class);
		if (count != null) query.limit(count);
		if (cursorString != null && !Objects.equals(cursorString, "")) {
			query = query.startAt(Cursor.fromWebSafeString(cursorString));
		}

		List<Spot> records = new ArrayList<>();
		QueryResultIterator<Spot> iterator = query.iterator();
		int num = 0;
		while (iterator.hasNext()) {
			records.add(iterator.next());
			if (count != null) {
				num++;
				if (num == count) break;
			}
		}

		//Find the next cursor
		if (cursorString != null && !Objects.equals(cursorString, "")) {
			Cursor cursor = iterator.getCursor();
			if (cursor != null) {
				cursorString = cursor.toWebSafeString();
			}
		}
		return CollectionResponse.<Spot>builder().setItems(records).setNextPageToken(cursorString).build();
	}

	/**
	 * This inserts a new <code>Spot</code> object.
	 * @param spot The object to be added.
	 * @return The object to be added.
	 */
	@ApiMethod(name = "insertSpot")
	public Spot insertSpot(Spot spot) throws ConflictException {
		//If if is not null, then check if it exists. If yes, throw an Exception
		//that it is already present
//        if (spot.getId() != null) {
		if (findRecord(spot.getId()) != null) {
			throw new ConflictException("Object already exists");
		}
//        }
		//Since our @Id field is a Long, Objectify will generate a unique value for us
		//when we use put
		ofy().save().entity(spot).now();
		return spot;
	}

	/**
	 * This updates an existing <code>Spot</code> object.
	 * @param spot The object to be added.
	 * @return The object to be updated.
	 */
	@ApiMethod(name = "updateSpot")
	public Spot updateSpot(Spot spot)throws NotFoundException {
		if (findRecord(spot.getId()) == null) {
			throw new NotFoundException("Spot Record does not exist");
		}
		ofy().save().entity(spot).now();
		return spot;
	}

	/**
	 * This deletes an existing <code>spot</code> object.
	 * @param id The id of the object to be deleted.
	 */
	@ApiMethod(name = "removeSpot")
	public void removeSpot(@Named("id") Long id) throws NotFoundException {
		Spot record = findRecord(id);
		if(record == null) {
			throw new NotFoundException("Spot Record does not exist");
		}
		ofy().delete().entity(record).now();
	}

	//Private method to retrieve a <code>Spot</code> record
	private Spot findRecord(Long id) {
		return ofy().load().type(Spot.class).id(id).now();
		//or return ofy().load().type(Spot.class).filter("id",id).first.now();
	}
}