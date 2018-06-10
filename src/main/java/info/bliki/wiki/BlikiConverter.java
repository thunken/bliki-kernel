package info.bliki.wiki;

import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import info.bliki.html.HTML2WikiConverter;
import info.bliki.html.wikipedia.ToWikipedia;
import info.bliki.util.Throwables;
import info.bliki.wiki.filter.PlainTextConverter;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.WikiModel;
import lombok.extern.slf4j.Slf4j;

/**
 * A converter tool for using the Wiki2HTML, Plain2Wiki and HTML2Wiki conversion functions in a Java Swing GUI.
 */
@Slf4j
public class BlikiConverter extends JFrame {

	class ClearListener implements java.awt.event.ActionListener {
		@Override
		public void actionPerformed(final java.awt.event.ActionEvent event) {
			input.setText("");
			output.setText("");
		}
	}

	class Html2WikiListener implements java.awt.event.ActionListener {
		@Override
		public void actionPerformed(final java.awt.event.ActionEvent event) {
			final String strData = input.getText();
			final HTML2WikiConverter conv = new HTML2WikiConverter(strData);
			final String result = conv.toWiki(new ToWikipedia(true, true, true));
			output.setText(result);
		}
	}

	class Wiki2HtmlListener implements java.awt.event.ActionListener {
		@Override
		public void actionPerformed(final java.awt.event.ActionEvent event) {
			final String strData = input.getText();
			final WikiModel wikiModel = new WikiModel(new Configuration(), Locale.ENGLISH, "${image}", "${title}");
			wikiModel.setUp();
			try {
				final String result = wikiModel.render(strData, false);
				output.setText(result);
			} catch (final IOException e) {
				Throwables.log(log, e);
			} finally {
				wikiModel.tearDown();
			}
		}
	}

	class Wiki2PlainListener implements java.awt.event.ActionListener {

		@Override
		public void actionPerformed(final java.awt.event.ActionEvent event) {
			final String strData = input.getText();
			final WikiModel wikiModel = new WikiModel(new Configuration(), Locale.ENGLISH, "${image}", "${title}");
			wikiModel.setUp();
			try {
				final String result = wikiModel.render(new PlainTextConverter(), strData, false);
				output.setText(result);
			} catch (final IOException e) {
				Throwables.log(log, e);
			} finally {
				wikiModel.tearDown();
			}
		}
	}

	private static final long serialVersionUID = 6600498097600405919L;

	private static void createBlikiConverter() {
		final BlikiConverter frame = new BlikiConverter("The bliki converter demo application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addComponentsToPane(frame.getContentPane());
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(final String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> createBlikiConverter());
	}

	private javax.swing.JButton wikiToHtmlButton;
	private javax.swing.JButton wikiToWikiPlaintext;
	private javax.swing.JButton htmlToWikiButton;
	private javax.swing.JButton clearButton;

	private javax.swing.JTextArea input;

	private javax.swing.JTextArea output;

	public BlikiConverter(final String name) {
		super(name);
	}

	private void addComponentsToPane(final Container container) {
		wikiToHtmlButton = new javax.swing.JButton("Wiki text to HTML");
		wikiToHtmlButton.addActionListener(new Wiki2HtmlListener());
		wikiToWikiPlaintext = new javax.swing.JButton("Wiki text to plain text");
		wikiToWikiPlaintext.addActionListener(new Wiki2PlainListener());
		htmlToWikiButton = new javax.swing.JButton("HTML to wiki text");
		htmlToWikiButton.addActionListener(new Html2WikiListener());
		clearButton = new javax.swing.JButton("Clear input/output");
		clearButton.addActionListener(new ClearListener());
		input = new javax.swing.JTextArea(20, 80);
		final JScrollPane inputSP = new JScrollPane(input);
		output = new javax.swing.JTextArea(20, 80);
		final JScrollPane outputSP = new JScrollPane(output);

		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

		container.add(inputSP);

		addButtonPanelToPane(container);
		container.add(outputSP);
	}

	private void addButtonPanelToPane(final Container container) {
		final JPanel horizontalButtonPanel = new JPanel();
		horizontalButtonPanel.setLayout(new java.awt.FlowLayout());
		horizontalButtonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		horizontalButtonPanel.add(wikiToHtmlButton);
		horizontalButtonPanel.add(htmlToWikiButton);
		horizontalButtonPanel.add(wikiToWikiPlaintext);
		horizontalButtonPanel.add(clearButton);
		container.add(horizontalButtonPanel);
	}
}
