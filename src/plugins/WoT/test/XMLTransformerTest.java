/* This code is part of WoT, a plugin for Freenet. It is distributed 
 * under the GNU General Public License, version 2 (or at your option
 * any later version). See http://www.gnu.org/ for details of the GPL. */
package plugins.WoT.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import freenet.keys.FreenetURI;

import plugins.WoT.Identity;
import plugins.WoT.OwnIdentity;
import plugins.WoT.XMLTransformer;
import plugins.WoT.exceptions.InvalidParameterException;
import plugins.WoT.exceptions.UnknownIdentityException;
import plugins.WoT.introduction.IntroductionPuzzle;

/**
 * A unit test for class {@link XMLTransformer}.
 * 
 * @author xor (xor@freenetproject.org)
 */
public class XMLTransformerTest extends DatabaseBasedTest {

	private XMLTransformer mTransformer;
	
	private OwnIdentity mOwnIdentity;
	
	protected void setUp() throws Exception {
		super.setUp();
	
		mTransformer = new XMLTransformer(mWoT);
		
		mOwnIdentity = mWoT.createOwnIdentity(
				"SSK@egaZBiTrPGsiLVBJGT91MOX5jtC6pFIDFDyjt3FcsRI,GDQlSg9ncBBF8XIS-cXYb-LM9JxE3OiSydyOaZgCS4k,AQECAAE/",
				"SSK@lY~N0Nk5NQpt6brGgtckFHPY11GzgkDn4VDszL6fwPg,GDQlSg9ncBBF8XIS-cXYb-LM9JxE3OiSydyOaZgCS4k,AQACAAE/",
				"test-identity", true, "Freetalk");
	}

	public void testExportOwnIdentity() {
		//fail("Not yet implemented"); // TODO
	}

	public void testImportIdentity() {
		//fail("Not yet implemented"); // TODO
	}

	public void testExportIntroduction() throws MalformedURLException, InvalidParameterException, TransformerException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		mTransformer.exportIntroduction(mOwnIdentity, os);
		
		String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
							+ "<WoT-testing>" 
							+ "<IdentityIntroduction Version=\"1\">"
							+ "<Identity URI=\"USK@lY~N0Nk5NQpt6brGgtckFHPY11GzgkDn4VDszL6fwPg,GDQlSg9ncBBF8XIS-cXYb-LM9JxE3OiSydyOaZgCS4k,AQACAAE/WoT/0\"/>"
							+ "</IdentityIntroduction>"
							+ "</WoT-testing>";
		
		assertEquals(expectedXML, os.toString().replaceAll("[\n\r]", ""));
	}

	public void testImportIntroduction() throws SAXException, IOException, InvalidParameterException {
		String introductionXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
			+ "<WoT-testing>" 
			+ "<IdentityIntroduction Version=\"1\">"
			+ "<Identity URI=\"USK@HH~V2XmCbZp~738qtE67jUg1M5L5flVvQfc2bYpE1o4,c8H39jkp08cao-EJVTV~rISHlcMnlTlpNFICzL4gmZ4,AQACAAE/WoT/0\"/>"
			+ "</IdentityIntroduction>"
			+ "</WoT-testing>";
		
		FreenetURI identityURI = new FreenetURI("USK@HH~V2XmCbZp~738qtE67jUg1M5L5flVvQfc2bYpE1o4,c8H39jkp08cao-EJVTV~rISHlcMnlTlpNFICzL4gmZ4,AQACAAE/WoT/0");
		
		ByteArrayInputStream is = new ByteArrayInputStream(introductionXML.getBytes("UTF-8"));
		
		try {
			mOwnIdentity.removeContext(IntroductionPuzzle.INTRODUCTION_CONTEXT); mWoT.storeAndCommit(mOwnIdentity);
			
			mTransformer.importIntroduction(mOwnIdentity, is);
			fail("XMLTransformer.importIntroduction() should not import an introduction if the puzzle publisher does not allow introduction anymore.");
		} catch (InvalidParameterException e) {
			try {
				mWoT.getIdentityByURI(identityURI);
				fail("XMLTransformer.importIntroduction() failed but the new identity exists.");
			}
			catch(UnknownIdentityException ex) {
				
			}
		}
		
		is = new ByteArrayInputStream(introductionXML.getBytes("UTF-8"));
		
		mOwnIdentity.addContext(IntroductionPuzzle.INTRODUCTION_CONTEXT); mWoT.storeAndCommit(mOwnIdentity);
		
		Identity importedIdentity = mTransformer.importIntroduction(mOwnIdentity, is);
		
		assertEquals(importedIdentity.getRequestURI(), identityURI);
		
		assertEquals(importedIdentity.getNickname(), null);
		
		assertEquals(importedIdentity.doesPublishTrustList(), false);
		
		assertEquals(importedIdentity.getContexts().size(), 0);
		
		assertEquals(importedIdentity.getProperties().size(), 0);
	}

	public void testExportIntroductionPuzzle() {
		//fail("Not yet implemented"); // TODO
	}

	public void testImportIntroductionPuzzle() {
		//fail("Not yet implemented"); // TODO
	}

}
