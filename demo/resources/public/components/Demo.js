var React = require('react');
var store = require('../store');
var Replies = require('./Replies');

var Demo = React.createClass({
	getInitialState: function() {
		return store.getState();
	},
	componentWillMount: function() {
		store.addChangeListener(this.onChange);
	},
	componentWillUnmount: function() {
		store.removeChangeListener(this.onChange);
	},
	onChange: function() {
		this.setState(store.getState());
	},
	onTyping: function(e) {
		store.getState().sentence = e.target.value;
		store.emitChange();
	},
	onSubmit: function(e) {
		console.debug("Sentence =", this.state.sentence);
		var url = "/score/";
		var sentence = this.state.sentence;

		$.ajax({
			url: url,
			method: 'post',
			data: {
				sentence: sentence,
			},
			dataType: 'json',
			success: function(reply) {
				console.debug(reply);
				store.save(reply);
				store.emitChange();
			},
			error: function(err) {
				alert("Failed.");
			}
		});
	},
	render: function() {
		return (
			<div>
				<h1>Sentence Scoring</h1>
				<hr />

				<div className="text-area">
					<textarea onChange={this.onTyping} 
								value={this.state.sentence} />
				</div>

				<button className="btn btn-primary" onClick={this.onSubmit}>
					Score
				</button>

				<Replies replies={this.state.replies} />
			</div>
		);
	}
});

module.exports = Demo;